plugins {
    id("com.android.library")
    kotlin("android")
}

apply(from = rootProject.file(".buildscript/configure_maven_publish.gradle"))

android {
    compileSdkVersion(Versions.compileSdk)

    defaultConfig {
        minSdkVersion(Versions.minSdk)
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
    implementation(project(":rx-location-manager"))
    api(project(":rx-location-manager-nmea"))
    base()
    reactive()
    test()
}
