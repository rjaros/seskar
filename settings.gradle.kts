rootProject.name = "seskar"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}

includeBuild("gradle-plugin")
