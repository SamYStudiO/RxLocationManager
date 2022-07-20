plugins {
    id("com.android.application") version Versions.android_build_tools apply false
    kotlin("android") version Versions.kotlin apply false
    kotlin("kapt") version Versions.kotlin apply false
    id("com.github.ben-manes.versions") version Versions.gradle_versions_plugin
    id("com.vanniktech.maven.publish") version Versions.gradle_maven_publish_plugin
    id("com.diffplug.spotless") version Versions.spotless
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            ktlint(Versions.ktlint).userData(mapOf("disabled_rules" to "no-wildcard-imports"))
        }
        kotlinGradle {
            target("**/*.gradle.kts")
            ktlint(Versions.ktlint).userData(mapOf("disabled_rules" to "no-wildcard-imports"))
        }
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            // Treat all Kotlin warnings as errors
            allWarningsAsErrors = true
        }
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
    resolutionStrategy {
        componentSelection {
            all {
                if (isNonStable(candidate.version) && !isNonStable(currentVersion)) {
                    reject("Release candidate")
                }
            }
        }
    }
}