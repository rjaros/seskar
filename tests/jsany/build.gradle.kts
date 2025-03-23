plugins {
    alias(kfc.plugins.library)
    alias(libs.plugins.seskar)
}

dependencies {
    commonMainImplementation(kotlinWrappers.js)

    commonTestImplementation(libs.kotlin.test)
    commonTestImplementation(libs.coroutines.core)
    commonTestImplementation(libs.coroutines.test)
}
