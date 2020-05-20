buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven { url = uri("https://maven.fabric.io/public") }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.android_build_tools}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("com.github.ben-manes:gradle-versions-plugin:${Versions.gradle_versions_plugin}")
        classpath("com.vanniktech:gradle-maven-publish-plugin:${Versions.gradle_maven_publish_plugin}")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    }
}

apply(plugin = "com.github.ben-manes.versions")

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