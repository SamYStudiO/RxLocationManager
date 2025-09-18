plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.spotless)
}

kotlin {
    jvmToolchain(
        libs.versions.jvmToolchain
            .get()
            .toInt(),
    )
    compilerOptions {
        allWarningsAsErrors = true
    }
}

android {
    namespace = "net.samystudio.rxlocationmanager.nmea.sample"
    compileSdk =
        libs.versions.compileSdk
            .get()
            .toInt()

    defaultConfig {
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.targetSdk
                .get()
                .toInt()
        applicationId = "net.samystudio.rxlocationmanager.nmea"
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint()
            .editorConfigOverride(
                mapOf(
                    "ktlint_code_style" to "ktlint_official",
                    "max_line_length" to "off",
                ),
            )
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}

dependencies {
    implementation(libs.bundles.base)
    implementation(libs.bundles.reactive)
    implementation(project(":rx-location-manager"))
    implementation(project(":rx-location-manager-altitude"))
    implementation(libs.rxpermissions)
}
