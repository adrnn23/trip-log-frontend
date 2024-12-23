plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.triplog"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.triplog"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
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
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.foundation.layout.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.material.icons.extended)

    implementation(libs.retrofit2.retrofit)
    implementation(libs.retrofit2.converter.moshi)


    implementation(libs.moshi.kotlin)
    implementation(libs.moshi)
    kapt(libs.moshi.kotlin.codegen)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.test)

    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.navigation.compose.v280)

    implementation(libs.androidx.security.crypto)

    implementation(libs.android)
    implementation(libs.maps.compose)

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.compose.v161)
    implementation(libs.coil.compose)
    implementation (libs.autofill)
    implementation (libs.discover)
    implementation (libs.place.autocomplete)
    implementation (libs.offline)
    implementation (libs.mapbox.search.android)
    implementation (libs.mapbox.search.android.ui)
    implementation (libs.mapbox.search.android)
    implementation (libs.converter.gson)
}