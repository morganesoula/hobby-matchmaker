plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.spm.kmp) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
}

val projectStructure = file(projectDir)
val configFile = files(rootDir.resolve("config/detekt/detekt.yml"))
val baselineFile = rootDir.resolve("config/detekt/baseline.xml")
val detektFiles = providers.gradleProperty("detektFiles")
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
    val only = detektFiles.orNull?.split(",")?.filter { it.isNotBlank() }
    if (only.isNullOrEmpty()) setSource(projectStructure) else setSource(files(only))
    config.setFrom(configFile)
    if (baselineFile.exists()) baseline.set(baselineFile)
    include(kotlinFiles)
    exclude(resourcesFiles, buildFiles)
    reports {
        html.required.set(true)
        sarif.required.set(true)
        xml.required.set(false)
        txt.required.set(false)
    }
}

// Records current issues in config/detekt/baseline.xml so detektAll only fails on new ones
tasks.register<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>("detektAllBaseline") {
    description = "Creates the DETEKT baseline for all modules"
    parallel = true
    buildUponDefaultConfig = true
    setSource(projectStructure)
    config.setFrom(configFile)
    baseline.set(baselineFile)
    include(kotlinFiles)
    exclude(resourcesFiles, buildFiles)
}
