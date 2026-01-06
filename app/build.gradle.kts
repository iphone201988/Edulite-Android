plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin1kept)
    id("kotlin-parcelize")
    alias(libs.plugins.google.services)
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.edu.lite"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.edu.lite"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.fragment.ktx.v277)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.databinding.runtime)
    implementation(libs.dagger)
    implementation(libs.gson)
    implementation (libs.retrofit)
    implementation (libs.okhttp)
    implementation (libs.logging.interceptor)
    implementation (libs.play.services.location)
    implementation (libs.glide)
    implementation (libs.sdp.android)
    implementation (libs.ssp.android)
    kapt(libs.dagger.compiler)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation (libs.lottie)
    implementation (libs.converter.gson)

    // mp chart
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-messaging-ktx:24.0.1")
    //svg images
    implementation  ("com.caverock:androidsvg:1.4")

// Room Database
    val roomVersion = "2.8.4"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")



    // exo player
    implementation("androidx.media3:media3-exoplayer:1.8.0")
    implementation("androidx.media3:media3-ui:1.8.0")

}

