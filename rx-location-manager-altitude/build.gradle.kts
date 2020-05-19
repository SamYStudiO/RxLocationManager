plugins {
    id("com.android.library")
    kotlin("android")
}

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
        buildConfig = true
    }
}

dependencies {
    implementation(project(":rx-location-manager"))
    api(project(":rx-location-manager-nmea"))
    base()
    test()
}

apply {
    from(rootProject.file("gradle/gradle-mvn-push.gradle"))
}