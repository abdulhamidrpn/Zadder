import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rpn.zadder"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rpn.zadder"
        minSdk = 21
        targetSdk = 35
        versionCode = 4
        versionName = "4.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String", "UNSPLASH_API_KEY", properties.getProperty("UNSPLASH_API_KEY"))

        buildConfigField("String", "ADMOB_BANNER_AD", properties.getProperty("ADMOB_BANNER_AD"))
        buildConfigField("String", "ADMOB_INTERSTITIAL_AD", properties.getProperty("ADMOB_INTERSTITIAL_AD"))
        buildConfigField("String", "ADMOB_REWARDED_AD", properties.getProperty("ADMOB_REWARDED_AD"))

        // Read admob_app_id from local.properties
        val admobAppId: String = properties.getProperty("ADMOB_APP_ID") ?: ""
        manifestPlaceholders["ADMOB_APP_ID"] = admobAppId
    }


    signingConfigs {
        // We use a bundled debug keystore, to allow debug builds from CI to be upgradable
        named("debug") {}
        create("release") {
            val keystorePropertiesFile = project.rootProject.file("local.properties")
            val properties = Properties()
            properties.load(keystorePropertiesFile.inputStream())

            storeFile = rootProject.file("keystore.jks")
            storePassword = properties["STORE_PASSWORD"] as String
            keyAlias = properties["KEY_ALIAS"] as String
            keyPassword = properties["KEY_PASSWORD"] as String
        }
    }

    buildTypes {

        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            isShrinkResources = true
            isMinifyEnabled = true
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

composeCompiler {
    enableStrongSkippingMode = true
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.localbroadcastmanager)

    //compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.animation)
    implementation(libs.coil.compose)

    //Google Ads
    implementation(libs.google.ads)

    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.crashlytics)
    implementation(libs.google.firebase.analytics)

    // Room
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)

    //Splash Screen
    implementation(libs.androidx.core.splashscreen)

    //Cloudy for blurring effect
    implementation(libs.cloudy)

    //Paging
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    // KotlinX Serialization
    implementation(libs.kotlinx.serialization.json)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    //Dagger-Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
}