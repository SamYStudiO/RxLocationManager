plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.gradleMavenPublish)
}

project.findProperty("GROUP")?.let { group = it }
project.findProperty("VERSION_NAME")?.let { version = it }

android {
    namespace = "net.samystudio.rxlocationmanager.altitude"
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
    implementation(project(":rx-location-manager"))
    api(project(":rx-location-manager-nmea"))
    implementation(libs.bundles.base)
    implementation(libs.bundles.reactive)
    testImplementation(libs.bundles.test)
}
