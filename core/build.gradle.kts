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
    id("org.jetbrains.intellij") version "1.1.4"
}

dependencies {
    implementation(project(":resources_zh"))
    implementation(project(":lang"))
    // https://mvnrepository.com/artifact/uk.com.robust-it/cloning
    implementation("uk.com.robust-it:cloning:1.9.12")
}

intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))
    downloadSources.set(properties("platformDownloadSources").toBoolean())
    updateSinceUntilBuild.set(false)
    sandboxDir.set("$rootDir.path/.idea-sandbox")

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

tasks {
    patchPluginXml {
        version.set(properties("pluginVersion") + "-" + properties("sdkSinceBuild"))
        sinceBuild.set(properties("sdkSinceBuild"))
        untilBuild.set(properties("sdkUntilBuild"))
    }
//    runPluginVerifier {
//        ideVersions.set(listOf((properties("platformType") + "-" + properties("platformVersion"))))
//        localPaths.from("$rootDir.path/IDEA_SDK_HOME")
//        downloadDir.set("$rootDir.path/IDEA_SDK_HOME")
//        verificationReportsDir.set("$rootDir.path/reports/pluginVerifier")
//    }
    register<Copy>("copyDependenciesToLibs") {
        dependsOn("jar")
        from(configurations.runtimeClasspath)
        into("${buildDir}/libs")
    }

}