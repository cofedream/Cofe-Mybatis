#!/usr/bin/env bash
# Platform compatibility check: "would this plugin throw NoClassDefFoundError /
# ClassNotFoundException inside IntelliJ IDEA <version>?"
#
# It does two things, because javac alone is not enough:
#   1. compiles every source set against the target IDE's own jars;
#   2. scans the produced bytecode and verifies that every referenced class is
#      actually reachable from a *plugin* classloader.
#
# Step 2 is the important one: the platform happily compiles against classes that
# are not on a plugin's classpath (JetBrains keeps legacy third-party classes in
# `*Shim` modules for platform-internal binary compatibility), so a clean compile
# can still crash at load time. Both failures of 2.1.2 on 2026.2 were of this kind.
#
# Usage:
#   scripts/check-platform-compat.sh                 # uses $IDE_HOME
#   IDE_HOME=/path/to/idea-IU scripts/check-platform-compat.sh
#
# Exit code 0 = safe to ship on that IDE build, 1 = broken.
set -uo pipefail

REPO=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
IDE_HOME=${IDE_HOME:-$HOME/.local/share/JetBrains/Toolbox/apps/intellij-idea-2}
WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT

if [ ! -d "$IDE_HOME/lib" ]; then
  echo "IDE_HOME=$IDE_HOME does not look like an IntelliJ installation" >&2
  exit 2
fi
echo "target IDE: $(cat "$IDE_HOME/build.txt" 2>/dev/null || echo "$IDE_HOME")"

# Jars the plugin ships itself (gradle `implementation`) are visible by definition.
# Keep this in sync with core/build.gradle.kts; cached on first run.
THIRD_PARTY="uk.com.robust-it:cloning:1.9.12"
CACHE=${XDG_CACHE_HOME:-$HOME/.cache}/cofe-mybatis-check
mkdir -p "$CACHE"
BUNDLED_JARS=""
for jar in $(find "$HOME/.m2" "$HOME/.gradle" "$REPO"/core/build -name 'cloning-*.jar' 2>/dev/null | head -5); do
  BUNDLED_JARS="$BUNDLED_JARS:$jar"
done
if [ -z "$BUNDLED_JARS" ]; then
  jar="$CACHE/cloning-1.9.12.jar"
  [ -f "$jar" ] || curl -sSLo "$jar" "https://repo1.maven.org/maven2/uk/com/robust-it/cloning/1.9.12/cloning-1.9.12.jar" || {
    echo "cannot resolve $THIRD_PARTY" >&2; exit 2; }
  BUNDLED_JARS=":$jar"
fi

CP=$(python3 - "$IDE_HOME" <<'PY'
import pathlib, sys
ide = pathlib.Path(sys.argv[1])
jars = list((ide / "lib").glob("*.jar")) + list((ide / "plugins").glob("*/lib/**/*.jar"))
print(":".join(str(j) for j in jars if "sources" not in j.name))
PY
)
[ -n "$BUNDLED_JARS" ] && CP="$CP${BUNDLED_JARS}"

SP="$REPO/core/src/main/java:$REPO/lang/src/main/java:$REPO/lang/src/main/gen"
{
  printf -- '-cp\n"%s"\n' "$CP"
  printf -- '-sourcepath\n"%s"\n' "$SP"
  find "$REPO/core/src/main/java" "$REPO/lang/src/main/java" "$REPO/lang/src/main/gen" -name '*.java'
} > "$WORK/javac.args"

echo "== step 1: compile against the target SDK =="
if ! javac --release 17 -encoding UTF-8 -nowarn -proc:none \
     -d "$WORK/out" @"$WORK/javac.args" > "$WORK/javac.log" 2>&1; then
  grep -E "错误|error" "$WORK/javac.log" | head -30
  echo "RESULT: BROKEN — sources do not compile against $IDE_HOME"
  exit 1
fi
echo "   ok"

echo "== step 2: every referenced class must be loadable by a plugin =="
printf '%s' "$CP" > "$WORK/cp.txt"
if ! python3 - "$IDE_HOME" "$WORK/out" "$WORK/cp.txt" <<'PY'
import concurrent.futures, pathlib, re, subprocess, sys, zipfile

ide, out, cp_file = pathlib.Path(sys.argv[1]), pathlib.Path(sys.argv[2]), pathlib.Path(sys.argv[3])
jars = [pathlib.Path(p) for p in cp_file.read_text().split(":") if p.endswith(".jar")]

visible, shim_only = {}, {}
for jar in jars:
    try:
        zf = zipfile.ZipFile(jar)
    except Exception:
        continue
    # JetBrains compat shims keep legacy third-party classes alive for the
    # platform only — a plugin classloader cannot see them.
    target = shim_only if jar.stem.endswith("Shim") else visible
    for name in zf.namelist():
        if name.endswith(".class"):
            target.setdefault(name[:-6], set()).add(jar.name)

OWN = ("tk/cofe/",)
NATIVE = ("java/", "javax/", "jdk/", "sun/", "com/sun/", "org/w3c/", "org/xml/")


def refs_of(cls):
    txt = subprocess.run(["javap", "-v", "-p", str(cls)], capture_output=True, text=True).stdout
    refs = set()
    for line in txt.splitlines():
        m = re.match(r"\s*#\d+ = Class\s+#\d+\s+//\s*(\S+)", line)
        if m:
            ref = m.group(1).strip('"')
            refs.add(ref[2:-1] if ref.startswith("[L") else ref)
        m = re.match(r"\s*super_class:\s+\S+\s+//\s+(\S+)", line)
        if m:
            refs.add(m.group(1).strip('"'))
    return cls, refs


classes = sorted(out.rglob("*.class"))
with concurrent.futures.ThreadPoolExecutor(max_workers=8) as pool:
    scanned = list(pool.map(refs_of, classes))

problems = []
for cls, refs in scanned:
    for ref in refs:
        if "/" not in ref or ref.startswith(OWN + NATIVE) or ref in visible:
            continue
        why = ("only in JetBrains-internal shim: " + ", ".join(sorted(shim_only[ref])[:2])
               if ref in shim_only else "missing from this IDE build entirely")
        problems.append(f"{cls.relative_to(out)}: {ref.replace('/', '.')} -> {why}")

if problems:
    for p in dict.fromkeys(problems):
        print("   FAIL", p)
    print("RESULT: BROKEN — plugin would throw NoClassDefFoundError at runtime")
    sys.exit(1)
print("   ok")
PY
then
  exit 1
fi

echo "RESULT: OK"
