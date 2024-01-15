object Deps {
    object Coroutines {
        private const val version = "1.7.3"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object Firebase {
        private const val firebaseVersion = "32.7.0"
        const val firebaseBom = "com.google.firebase:firebase-bom:$firebaseVersion"
        const val firebaseAuth = "com.google.firebase:firebase-auth"

        private const val firebaseUiAuthVersion = "7.2.0"
        const val firebaseUiAuth = "com.firebaseui:firebase-ui-auth:$firebaseUiAuthVersion"
    }

    object Android {
        const val material = "com.google.android.material:material:1.11.0"
    }

    object AndroidX {
        const val appCompat = "androidx.appcompat:appcompat:1.6.1"
        const val coreKtx = "androidx.core:core-ktx:1.12.0"

        object Activity {
            const val activityKtx = "androidx.activity:activity-ktx:1.8.2"
        }

        object Compose {
            const val material3 = "androidx.compose.material3:material3"
            const val preview = "androidx.compose.ui:ui-tooling-preview"
            const val uiTest = "androidx.compose.ui:ui-test-junit4"
            const val activity = "androidx.activity:activity-compose:1.8.2"
            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2"
        }

        object Lifecycle {
            private const val version = "2.6.2"
            const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        }

        object Navigation {
            private const val version = "2.7.6"
            const val ui = "androidx.navigation:navigation-ui-ktx:$version"
            const val commonKtx = "androidx.navigation:navigation-common-ktx:$version"
        }

        object Room {
            private const val version = "2.6.1"
            const val runtime = "androidx.room:room-runtime:$version"
            const val ktx = "androidx.room:room-ktx:$version"
            const val compiler = "androidx.room:room-compiler:$version"
        }
    }

    object Dagger {
        private const val version = "2.50"
        const val hiltAndroid = "com.google.dagger:hilt-android:$version"
        const val hiltAndroidCompiler = "com.google.dagger:hilt-compiler:$version"
        const val hiltAndroidTesting = "com.google.dagger:hilt-android-testing:$version"
    }

    object OkHttp {
        private const val version = "4.12.0"
        const val okHttp = "com.squareup.okhttp3:okhttp:$version"
        const val logging = "com.squareup.okhttp3:logging-interceptor:$version"
    }

    object Retrofit {
        private const val version = "2.9.0"
        const val retrofit = "com.squareup.retrofit2:retrofit:$version"
        const val moshiConverter = "com.squareup.retrofit2:converter-moshi:$version"
    }

    object Coil {
        private const val version = "2.5.0"
        const val coilCompose = "io.coil-kt:coil:$version"
    }
}

object TestDeps {
    object AndroidX {
        private const val version = "1.5.0"
        const val coreKtx = "androidx.test:core-ktx:$version"
        const val rules = "androidx.test:rules:$version"
        const val coreTesting = "androidx.arch.core:core-testing:2.2.0"
        const val androidX_jUnit = "androidx.test.ext:junit-ktx:1.1.5"
        const val navigationTest = "androidx.navigation:navigation-testing:2.7.6"
    }

    object Coroutines {
        private const val version = "1.7.3"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object JUnit {
        private const val version = "4.13.2"
        const val junit = "junit:junit:$version"
    }

    object MockK {
        const val mockK = "io.mockk:mockk:1.13.9"
    }

    const val truth = "com.google.truth:truth:1.2.0"
}