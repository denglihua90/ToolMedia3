plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.dlh.toolmedia3"
    compileSdk {
        version = release(36)
    }
    defaultConfig {
        applicationId = "com.dlh.toolmedia3"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(project(":DLHPlayer"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    var media3_version = "1.9.0"
    // Media3 dependencies
    // For media playback using ExoPlayer
    implementation("androidx.media3:media3-exoplayer:${media3_version}")
    // For DASH playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-dash:${media3_version}")
    // For HLS playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-hls:${media3_version}")
    // For SmoothStreaming playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-smoothstreaming:${media3_version}")
    // For RTSP playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-rtsp:${media3_version}")
    // For loading data using the OkHttp network stack
    implementation("androidx.media3:media3-datasource-okhttp:${media3_version}")
    // For loading data using librtmp
    implementation("androidx.media3:media3-datasource-rtmp:${media3_version}")
    // Common functionality
    implementation("androidx.media3:media3-common:${media3_version}")
    implementation("androidx.media3:media3-datasource:${media3_version}")
    implementation("androidx.media3:media3-extractor:${media3_version}")
    // For UI components
    implementation("androidx.media3:media3-ui:${media3_version}")
//    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    // XPopup库
    implementation("com.github.li-xiaojun:XPopup:2.10.0") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-android-extensions-runtime")
    }
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    // Epoxy库
    implementation("com.airbnb.android:epoxy:5.2.0")
    annotationProcessor("com.airbnb.android:epoxy-processor:5.2.0")

}