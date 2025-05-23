import org.gradle.api.JavaVersion

object ProjectApplication {
    const val PROJECT_APPLICATION_ID = "com.msoula.hobbymatchmaker"
    const val PROJECT_APPLICATION_VERSION_NAME = "1.0.0"
    const val PROJECT_APPLICATION_VERSION_CODE = 1
}

object ProjectConfig {
    const val PROJECT_CONFIG_SDK_VERSION = 35
    const val PROJECT_CONFIG_MIN_SDK_VERSION = 26
    const val PROJECT_CONFIG_TARGET_SDK_VERSION = 35
    val PROJECT_CONFIG_JAVA_VERSION = JavaVersion.VERSION_21
}

object Modules {
    const val AUTHENTICATION_DATA = ":core:authentication:data"
    const val AUTHENTICATION_DOMAIN = ":core:authentication:domain"
    const val COMMON = ":core:common"
    const val DATABASE = ":core:database"
    const val DESIGN = ":core:design"
    const val DI = ":core:di"
    const val LOGIN_DOMAIN = ":core:login:domain"
    const val LOGIN_PRESENTATION = ":core:login:presentation"
    const val MOVIE_DATA = ":features:movies:data"
    const val MOVIE_DOMAIN = ":features:movies:domain"
    const val MOVIE_PRESENTATION = ":features:movies:presentation"
    const val MOVIE_DETAIL_DATA = ":features:moviedetail:data"
    const val MOVIE_DETAIL_DOMAIN = ":features:moviedetail:domain"
    const val MOVIE_DETAIL_PRESENTATION = ":features:moviedetail:presentation"
    const val NAVIGATION_DOMAIN = ":core:navigation:domain"
    const val NAVIGATION_PRESENTATION = ":core:navigation:presentation"
    const val NETWORK = ":core:network"
    const val SESSION_DATA = ":core:session:data"
    const val SESSION_DOMAIN = ":core:session:domain"
    const val SHARED = ":shared"
    const val SPLASHSCREEN_PRESENTATION = ":core:splashscreen:presentation"
    const val TEST_COMMON = ":testUtils:common"
}
