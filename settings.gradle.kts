pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") }
    }
}

include(
    ":rx-location-manager",
    ":rx-location-manager-altitude",
    ":rx-location-manager-nmea",
    ":rx-location-manager-sample",
    ":rx-location-manager-altitude-sample",
    ":rx-location-manager-nmea-sample"
)