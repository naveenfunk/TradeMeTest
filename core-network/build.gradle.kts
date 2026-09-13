plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

val secretsProperties = providers.fileContents(
    rootProject.layout.projectDirectory.file("secrets.properties"),
).asText.map { text ->
    text.lineSequence()
        .map(String::trim)
        .filter { it.isNotEmpty() && !it.startsWith("#") && it.contains('=') }
        .associate { line ->
            val (key, value) = line.split("=", limit = 2)
            key.trim() to value.trim()
        }
}.orElse(emptyMap())

fun secret(key: String): String = secretsProperties.get()[key].orEmpty()

android {
    namespace = "nz.co.trademetest.core.network"
    resourcePrefix = "network_"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 28

        buildConfigField("String", "TRADEME_BASE_URL", "\"https://api.tmsandbox.co.nz/v1/\"")
        buildConfigField("String", "TRADEME_CONSUMER_KEY", "\"${secret("TRADEME_CONSUMER_KEY")}\"")
        buildConfigField("String", "TRADEME_CONSUMER_SECRET", "\"${secret("TRADEME_CONSUMER_SECRET")}\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.okhttp.bom))
    api(libs.retrofit)
    api(libs.retrofit.converter.kotlinx.serialization)
    api(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.okhttp.mockwebserver)
}
