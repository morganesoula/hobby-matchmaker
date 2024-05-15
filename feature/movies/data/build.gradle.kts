import com.android.build.api.variant.BuildConfigField
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Properties

plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.DAGGER_HILT)
    kotlin(Plugins.SERIALIZATION) version PluginVersion.SERIALIZATION
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

androidComponents {
    onVariants {
        it.buildConfigFields.put(
            "tmdb_key",
            BuildConfigField("String", getTMDBKey(), "get tmdb key"),
        )
    }
}

android {
    namespace = "com.msoula.hobbymatchmaker.features.movies.data"
}

dependencies {
    // Arrow
    implementation(libs.arrow.core)

    // Core
    implementation(libs.core.ktx)
    implementation(libs.runtime)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Modules
    implementation(project(Modules.NETWORK))
    implementation(project(Modules.MOVIE_DOMAIN))
    implementation(project(Modules.DAO))
    implementation(project(Modules.COMMON))

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Room
    api(libs.room.runtime)
    kapt(libs.room.compiler)
    implementation(libs.room.ktx)
}
