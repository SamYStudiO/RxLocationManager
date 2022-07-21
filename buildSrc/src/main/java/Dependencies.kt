import org.gradle.api.Action
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.accessors.runtime.addExternalModuleDependencyTo

object Dependencies {
    // kotlin
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    // android
    const val core_ktx = "androidx.core:core-ktx:${Versions.core_ktx}"
    const val startup = "androidx.startup:startup-runtime:${Versions.startup}"
    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"

    // reactive
    const val rxjava = "io.reactivex.rxjava3:rxjava:${Versions.rxjava}"
    const val rxandroid = "io.reactivex.rxjava3:rxandroid:${Versions.rxandroid}"
    const val rxlocationmanager =
        "net.samystudio.rxlocationmanager:rxlocationmanager:${Versions.rxlocationmanager}"
    const val rxlocationmanager_altitude =
        "net.samystudio.rxlocationmanager:rxlocationmanager-altitude:${Versions.rxlocationmanager}"

    //misc
    const val rxpermissions = "com.github.tbruyelle:rxpermissions:${Versions.rxpermissions}"

    // test
    const val junit = "junit:junit:${Versions.junit}"
    const val mockk = "io.mockk:mockk:${Versions.mockk}"
}

fun DependencyHandler.base() {
    implementation(Dependencies.kotlin)
    implementation(Dependencies.core_ktx)
    implementation(Dependencies.startup)
    implementation(Dependencies.appcompat)
}

fun DependencyHandler.reactive() {
    api(Dependencies.rxjava)
    api(Dependencies.rxandroid)
}

fun DependencyHandler.test() {
    testImplementation(Dependencies.junit)
    testImplementation(Dependencies.mockk)
}

private fun DependencyHandler.implementation(depName: String) {
    add("implementation", depName)
}

fun DependencyHandler.implementation(
    group: String,
    name: String,
    version: String? = null,
    configuration: String? = null,
    classifier: String? = null,
    ext: String? = null,
    dependencyConfiguration: Action<ExternalModuleDependency>? = null
): ExternalModuleDependency = addExternalModuleDependencyTo(
    this,
    "implementation",
    group,
    name,
    version,
    configuration,
    classifier,
    ext,
    dependencyConfiguration
)

private fun DependencyHandler.api(depName: String) {
    add("api", depName)
}

private fun DependencyHandler.kapt(depName: String) {
    add("kapt", depName)
}

private fun DependencyHandler.compileOnly(depName: String) {
    add("compileOnly", depName)
}

private fun DependencyHandler.debugImplementation(depName: String) {
    add("debugImplementation", depName)
}

private fun DependencyHandler.testImplementation(depName: String) {
    add("testImplementation", depName)
}

private fun DependencyHandler.androidTestImplementation(depName: String) {
    add("androidTestImplementation", depName)
}