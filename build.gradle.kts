plugins {
    id("com.android.application") version Versions.android_build_tools apply false
    kotlin("android") version Versions.kotlin apply false
    kotlin("kapt") version Versions.kotlin apply false
    id("com.vanniktech.maven.publish") version Versions.gradle_maven_publish_plugin apply false
    id("com.diffplug.spotless") version Versions.spotless apply false
    id("com.github.ben-manes.versions") version Versions.gradle_versions_plugin
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            ktlint()
                .setUseExperimental(true)
                .editorConfigOverride(
                    mapOf(
                        "code_style" to "android",
                        "disabled_rules" to "no-wildcard-imports",
                        "max_line_length" to "off",
                        "ij_kotlin_allow_trailing_comma" to true,
                        "ij_kotlin_allow_trailing_comma_on_call_site" to true,
                    )
                )
        }
        kotlinGradle {
            target("**/*.gradle.kts")
            ktlint()
                .editorConfigOverride(
                    mapOf(
                        "ij_kotlin_allow_trailing_comma" to true,
                        "ij_kotlin_allow_trailing_comma_on_call_site" to true,
                    )
                )
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