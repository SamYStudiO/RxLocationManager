plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
        applicationId = "net.samystudio.rxlocationmanager.altitude"
        versionCode = 1
        versionName = "1.0"
    }

    sourceSets {
        getByName("main").java.srcDir("src/main/kotlin")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    base()
    implementation(Dependencies.rxlocationmanager)
    implementation(Dependencies.rxpermissions)
}
