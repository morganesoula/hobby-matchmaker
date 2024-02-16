buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2")
        classpath("androidx.compose.runtime:runtime:1.6.1")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
        classpath("com.google.gms:google-services:${PluginVersion.G_SERVICES}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    }
}
