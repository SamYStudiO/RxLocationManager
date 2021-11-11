plugins {
    id("com.android.library")
    kotlin("android")
}

apply(from = rootProject.file(".buildscript/configure_maven_publish.gradle"))

android {
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
    }

    sourceSets {
        getByName("main").java.srcDir("src/main/kotlin")
        getByName("test").java.srcDir("src/test/kotlin")
    }

    buildFeatures {
        buildConfig = false
    }
}

dependencies {
    base()
    test()
}
