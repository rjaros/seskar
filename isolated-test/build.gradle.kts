plugins {
    kotlin("js") version "1.8.0"
    id("io.github.turansky.kfc.application") version "5.80.0"
    id("io.github.turansky.seskar") version "0.30.0"
}

dependencies {
    implementation("io.github.turansky.seskar:seskar-core:0.30.0")

    testImplementation(kotlin("test-js"))
}

tasks.wrapper {
    gradleVersion = "7.6"
}
