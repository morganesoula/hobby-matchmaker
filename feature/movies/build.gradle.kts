import com.android.build.api.variant.BuildConfigField
import extensions.featureMoviesModuleDeps
import extensions.unitTestDeps
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Properties

plugins {
    `android-library`
    `kotlin-android`
    id(Plugins.DAGGER_HILT)
    kotlin(Plugins.SERIALIZATION) version PluginVersion.SERIALIZATION
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
            BuildConfigField("String", getTMDBKey(), "get tmdb key")
        )
    }
}

android {
    namespace = "com.example.movies"
}

dependencies {
    featureMoviesModuleDeps()
    unitTestDeps()
}
