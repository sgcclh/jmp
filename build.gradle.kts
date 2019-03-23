/*
 *    Copyright 2019 Django Cass
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.20-eap-52"
    id("com.github.johnrengelman.shadow") version "4.0.3"
    application
}

group = "com.django"
version = "2.1"

apply(plugin = "java")

ant.importBuild("version.xml")

repositories {
    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
    maven(url = "https://dl.bintray.com/kotlin/exposed")
    maven(url = "https://jitpack.io")
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("com.gitlab.django-sandbox:log2:8b941edd1a")
    implementation("com.github.djcass44:fav2:0.2.0")

    implementation("io.javalin:javalin:2.7.0")
    implementation("org.slf4j:slf4j-simple:1.7.25")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
    implementation("com.beust:klaxon:5.0.1")
    implementation("info.debatty:java-string-similarity:1.1.0")
    implementation("com.amdelamar:jhash:2.1.0")
    implementation("io.github.rybalkinsd:kohttp:0.7.1")
    implementation("com.google.code.gson:gson:2.8.5")

    implementation("commons-cli:commons-cli:1.4")

    implementation("com.auth0:java-jwt:3.7.0")

    implementation("org.jetbrains.exposed:exposed:0.11.2")
    runtimeOnly("org.xerial:sqlite-jdbc:3.25.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.2.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.2.0")
}

application {
    mainClassName = "com.django.jmp.api.RunnerKt"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<ShadowJar> {
    archiveName = "jmp.$extension"
}
tasks.withType<Test> {
    useJUnitPlatform()
}