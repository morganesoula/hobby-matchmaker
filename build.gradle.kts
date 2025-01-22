plugins {
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.detekt) apply false
}

val projectStructure = file(projectDir)
val configFile = files("$rootProject/../config/detekt/detekt.yml")
val kotlinFiles = "**/*.kt"
val resourcesFiles = "**/resources/**"
val buildFiles = "**/build/**"

tasks.register<io.gitlab.arturbosch.detekt.Detekt>("detektAll") {
    val autoFix = project.hasProperty("detektAutoFix")

    description = "Custom DETEKT build for all modules"
    parallel = true
    ignoreFailures = false
    autoCorrect = autoFix
    buildUponDefaultConfig = true
    setSource(projectStructure)
    config.setFrom(configFile)
    include(kotlinFiles)
    exclude(resourcesFiles, buildFiles)
    reports {
        html.required.set(true)
        xml.required.set(false)
        txt.required.set(false)
    }
}
