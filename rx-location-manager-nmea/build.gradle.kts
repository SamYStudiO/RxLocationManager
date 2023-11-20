plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

project.findProperty("GROUP")?.let { group = it }
project.findProperty("VERSION_NAME")?.let { version = it }

android {
    namespace = "net.samystudio.rxlocationmanager.nmea"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    sourceSets {
        getByName("main").java.srcDir("src/main/kotlin")
        getByName("test").java.srcDir("src/test/kotlin")
    }

    buildFeatures {
        buildConfig = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.bundles.base)
    testImplementation(libs.bundles.test)
}
