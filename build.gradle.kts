/*
 * Copyright (C) 2019-2022 cofe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * Copyright (C) 2019-2021 cofe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
fun properties(key: String) = project.findProperty(key).toString()

plugins {
    java
    id("org.jetbrains.intellij") version "1.9.0" apply (false)
}

group = properties("group")
version = (properties("pluginVersion") + "-" + properties("sdkSinceBuild"))

allprojects {
    repositories {
        mavenCentral()
    }
    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            sourceCompatibility = "11"
            targetCompatibility = "11"
            options.compilerArgs.addAll(
                listOf(
                    "-Xlint:deprecation"
                )
            )
        }
    }
}

tasks {
    create<Zip>("releaseVersion") {
        dependsOn(":core:copyDependenciesToLibs")
        from("core/build/libs")
        into(properties("pluginName") + "/lib")
        destinationDirectory.set(file("/"))
        archiveBaseName.set(properties("pluginName"))
        archiveVersion.set(properties("pluginVersion"))
        archiveClassifier.set(properties("sdkSinceBuild"))
        archiveExtension.set("zip")
        doLast {
            delete("resources_zh/build", "lang/build", "core/build", "build")
        }
    }
}