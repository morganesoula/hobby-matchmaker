buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
        classpath("com.google.gms:google-services:${PluginVersion.G_SERVICES}")
    }
}
