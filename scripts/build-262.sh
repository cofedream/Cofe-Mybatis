#!/usr/bin/env bash
# Build the installable plugin zip against IntelliJ IDEA 2026.2 (build 262).
#
# Needed workarounds (nothing here can be avoided with the legacy
# `org.jetbrains.intellij` 1.15.0 Gradle plugin):
#
#   1. javac must be 25 — 2026.2 platform jars are class-file v69 (Java 25),
#      javac 17 refuses to read them ("类文件具有错误的版本 69.0, 应为 61.0").
#      Source/target stays 17, so the produced bytecode is still Java 17.
#   2. Gradle itself must run on <= 21 — Gradle 8.2.1's bundled Kotlin 1.8
#      cannot parse the JVM version string of Java 25 ("IllegalArgumentException: 25.0.2").
#      => Gradle runs on 17, the JavaCompile tasks use a Java 25 toolchain.
#   3. IntelliJ 2024.4+ splits platform code into `<plugin>/lib/modules/*.jar`.
#      The 1.x Gradle plugin only globs `<plugin>/lib/*.jar`, so `:lang` and
#      `:core` would miss classes (e.g. com.siyeh.ig.psiutils.CollectionUtils,
#      com.intellij.spring.core.analysis.jvm.providers.*). We add those nested
#      jars as `compileOnly` (they must NOT be packaged).
#   4. `buildSearchableOptions` crashes on this platform ("Index: 1, Size: 1").
#      Skipping it only costs Settings-search entries for the settings page.
#
# Output: core/build/distributions/Cofe-Mybatis-<version>.zip
#
# Usage: scripts/build-262.sh [extra gradle args]
set -euo pipefail

REPO=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "$REPO"

GRADLE_JAVA=${GRADLE_JAVA:-$HOME/.local/share/mise/installs/java/17.0.2}
COMPILE_JAVA=${COMPILE_JAVA:-$HOME/.local/share/mise/installs/java/25.0.2}

[ -x "$GRADLE_JAVA/bin/javac" ] || { echo "GRADLE_JAVA=$GRADLE_JAVA not a JDK (需要 17)" >&2; exit 2; }
[ -x "$COMPILE_JAVA/bin/javac" ] || { echo "COMPILE_JAVA=$COMPILE_JAVA not a JDK (需要 25)" >&2; exit 2; }

# The IDE that the Gradle plugin downloaded for platformVersion (see gradle.properties).
IDE_DIR=$(ls -d "$HOME"/.gradle/caches/modules-2/files-2.1/com.jetbrains.intellij.idea/ideaIU/*/*/ideaIU-* 2>/dev/null | head -1)
if [ -z "$IDE_DIR" ]; then
  echo "没有找到 Gradle 下载的 IDEA 平台，先跑一次 ./gradlew :core:verifyPluginConfiguration" >&2
  exit 2
fi
echo "platform: $(cat "$IDE_DIR/build.txt" 2>/dev/null)  ($IDE_DIR)"

INIT=$(mktemp --suffix=.gradle.kts)
trap 'rm -f "$INIT"' EXIT
cat > "$INIT" <<'EOF'
allprojects {
    plugins.withId("java") {
        extensions.configure<JavaPluginExtension> {
            toolchain { languageVersion.set(JavaLanguageVersion.of(25)) }
        }
        val extraDir = providers.gradleProperty("extraModulesDir").orNull
        if (extraDir != null) {
            dependencies.add(
                "compileOnly",
                files(fileTree(extraDir) {
                    include("plugins/*/lib/modules/*.jar", "plugins/*/lib/*/*.jar", "lib/*.jar")
                })
            )
        }
    }
}
EOF

JAVA_HOME="$GRADLE_JAVA" ./gradlew buildPlugin \
  -I "$INIT" \
  -PextraModulesDir="$IDE_DIR" \
  -Porg.gradle.java.installations.paths="$COMPILE_JAVA" \
  -x buildSearchableOptions \
  --console=plain "$@"

VERSION=$(grep -E '^pluginVersion=' gradle.properties | cut -d= -f2)
SINCE=$(grep -E '^sdkSinceBuild=' gradle.properties | cut -d= -f2)
ZIP="core/build/distributions/Cofe-Mybatis.zip"
cp "$ZIP" "core/build/distributions/Cofe-Mybatis-${VERSION}-${SINCE}.zip"
echo
echo "OK -> core/build/distributions/Cofe-Mybatis-${VERSION}-${SINCE}.zip"
echo "安装: IDEA -> Settings -> Plugins -> ⚙ -> Install Plugin from Disk... 选这个 zip"
