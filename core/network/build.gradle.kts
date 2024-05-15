import com.android.build.api.variant.BuildConfigField
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Properties

plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.DAGGER_HILT)
    kotlin(Plugins.KAPT)
}

fun getTMDBKey(): String {
    val propFile = rootProject.file("./tmdb.properties")

    if (propFile.exists()) {
        val properties = Properties()
        properties.load(FileInputStream(propFile))
        return properties.getProperty("tmdb_key")
    } else {
        throw FileNotFoundException()
    }
}

apply<MainGradlePlugin>()

android {
    namespace = "com.msoula.hobbymatchmaker.core.network"
}

androidComponents {
    onVariants {
        it.buildConfigFields.put(
            "tmdb_key",
            BuildConfigField("String", getTMDBKey(), "get tmdb key"),
        )
    }
}

dependencies {
    // Core
    implementation(libs.runtime)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firebase.auth)

    // Google
    implementation(libs.play.services.auth)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Modules
    implementation(project(Modules.DESIGN))
    implementation(project(Modules.COMMON))

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
}
