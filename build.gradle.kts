plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

group = "camp.nextstep.edu"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.8.1")
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.10.2")
    testImplementation("org.assertj", "assertj-core", "3.25.3")
    testImplementation("io.kotest", "kotest-runner-junit5", "5.8.0")
    testImplementation("org.jetbrains.kotlinx", "kotlinx-coroutines-test", "1.8.1")
}

tasks {
    test {
        useJUnitPlatform()
    }
    ktlint {
        verbose.set(true)
    }
}
