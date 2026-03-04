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

    // BaseRecyclerViewAdapterHelper库
    implementation("com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.4")
    // 沉浸式状态栏和沉浸式导航栏管理: 对原库进行 Android 15、16 版本适配
    // https://github.com/OCNYang/ImmersionBar
    // 基础依赖包，必须要依赖
    implementation("com.github.OCNYang.ImmersionBar:immersionbar:3.4.6")
    // kotlin扩展（可选）
    implementation("com.github.OCNYang.ImmersionBar:immersionbar-ktx:3.4.6")

    // Room 依赖
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // SQLCipher 依赖（用于数据库加密）
    implementation("net.zetetic:android-database-sqlcipher:4.5.4")

    // 测试依赖
    testImplementation("androidx.room:room-testing:2.6.1")

    // 核心网络库
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    // Gson 解析容错：https://github.com/getActivity/GsonFactory
    implementation("com.github.getActivity:GsonFactory:10.5")
// Json 解析框架：https://github.com/google/gson
    implementation("com.google.code.gson:gson:2.13.2") {
        exclude(group = "com.google.errorprone", module = "error_prone_annotations")
    }
// Kotlin 反射库：用于反射 Kotlin data class 类对象，1.5.10 请修改成当前项目 Kotlin 的版本号
    implementation(libs.kotlin.reflect)

    // 工具库
    debugImplementation("com.github.chuckerteam.chucker:library:4.0.0")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:4.0.0")

    // 缓存库
    implementation("com.jakewharton:disklrucache:2.0.2")

}