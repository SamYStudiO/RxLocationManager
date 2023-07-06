plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "net.samystudio.rxlocationmanager.altitude.sample"
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

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    base()
    implementation(project(":rx-location-manager"))
    implementation(project(":rx-location-manager-altitude"))
    implementation(Dependencies.rxpermissions)
}
