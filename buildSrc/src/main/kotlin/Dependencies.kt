object Deps {
    object Coroutines {
        private const val version = "1.7.3"
        const val CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object Firebase {
        private const val firebaseVersion = "32.7.0"
        const val FIREBASE_BOM = "com.google.firebase:firebase-bom:$firebaseVersion"
        const val FIREBASE_AUTH = "com.google.firebase:firebase-auth"
        const val FIREBASE_CORE_KTX = "com.google.firebase:firebase-auth-ktx:22.3.0"

        private const val firebaseUiAuthVersion = "7.2.0"
        const val FIREBASE_UI_AUTH = "com.firebaseui:firebase-ui-auth:$firebaseUiAuthVersion"
    }

    object Google {
        private const val version = "20.7.0"
        const val PLAY_SERVICES = "com.google.android.gms:play-services-auth:$version"
    }

    object AndroidX {
        const val APP_COMPAT = "androidx.appcompat:appcompat:1.6.1"
        const val CORE_KTX = "androidx.core:core-ktx:1.12.0"

        object Activity {
            const val ACTIVITY_KTX = "androidx.activity:activity-ktx:1.8.2"
        }

        object Compose {
            private const val version = "1.6.0"

            const val MATERIAL = "androidx.compose.material:material:$version"
            const val MATERIAL3 = "androidx.compose.material3:material3:1.2.0-rc01"
            const val PREVIEW = "androidx.compose.ui:ui-tooling:$version"
            const val UI_TEST = "androidx.compose.ui:ui-test-junit4"
            const val ACTIVITY = "androidx.activity:activity-compose:1.8.2"
            const val VIEW_MODEL = "androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2"
        }

        object Lifecycle {
            private const val version = "2.6.2"
            const val LIFECYCLE_RUNTIME = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val VIEW_MODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        }

        object Navigation {
            private const val version = "2.7.6"
            const val NAVIGATION_UI = "androidx.navigation:navigation-ui-ktx:$version"
            const val COMPOSE_NAVIGATION = "androidx.navigation:navigation-compose:$version"
        }

        object Room {
            private const val version = "2.6.1"
            const val RUNTIME = "androidx.room:room-runtime:$version"
            const val ROOM_KTX = "androidx.room:room-ktx:$version"
            const val COMPILER = "androidx.room:room-compiler:$version"
        }
    }

    object Dagger {
        private const val version = "2.50"
        const val HILT_ANDROID = "com.google.dagger:hilt-android:$version"
        const val HILT_ANDROID_COMPILER = "com.google.dagger:hilt-compiler:$version"
        const val HILT_NAVIGATION_COMPOSE = "androidx.hilt:hilt-navigation-compose:1.1.0"
    }

    object OkHttp {
        private const val version = "4.12.0"
        const val OK_HTTP = "com.squareup.okhttp3:okhttp:$version"
        const val LOGGING = "com.squareup.okhttp3:logging-interceptor:$version"
    }

    object Retrofit {
        private const val version = "2.9.0"
        const val RETROFIT = "com.squareup.retrofit2:retrofit:$version"
        const val MOSHI_CONVERTER = "com.squareup.retrofit2:converter-moshi:$version"
    }

    object Coil {
        private const val version = "2.5.0"
        const val COIL_COMPOSE = "io.coil-kt:coil:$version"
    }
}

object TestDeps {
    object AndroidX {
        private const val version = "1.5.0"
        const val CORE_KTX = "androidx.test:core-ktx:$version"
        const val RULES = "androidx.test:rules:$version"
        const val CORE_TESTING = "androidx.arch.core:core-testing:2.2.0"
        const val ANDROIDX_JUNIT = "androidx.test.ext:junit-ktx:1.1.5"
        const val NAVIGATION_TEST = "androidx.navigation:navigation-testing:2.7.6"
    }

    object AssertK {
        private const val version = "0.28.0"
        const val ASSERTK = "com.willowtreeapps.assertk:assertk:$version"
    }

    object Coroutines {
        private const val version = "1.7.3"
        const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object Dagger {
        private const val version = "2.50"
        const val HILT_ANDROID_TESTING = "com.google.dagger:hilt-android-testing:$version"
    }

    object MockK {
        const val MOCKK = "io.mockk:mockk:1.13.9"
    }

    const val TRUTH = "com.google.truth:truth:1.2.0"
}
