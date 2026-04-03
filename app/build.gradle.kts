// FILE_PATH: app/build.gradle.kts
// ACTION: OVERWRITE
// DESCRIPTION: Locked-down Gradle file with WorkManager and JLibTorrent
// ---------------------------------------------------------
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.epicgera.vtrae"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.epicgera.vtrae"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // OpenSubtitles API key (consumer: JDB9891, anonymous access enabled)
        buildConfigField("String", "OPENSUBTITLES_API_KEY", "\"8fTdDxazunUUKcLCd8YzSMLN6Ad5uEul\"")

        // Google Drive Cloud Videos
        buildConfigField("String", "GOOGLE_DRIVE_API_KEY", "\"AIzaSyDaT8aVmZEImNxZ2XSJL8fWuX01UsVB2yg\"")
        buildConfigField("String", "GOOGLE_DRIVE_FOLDER_ID", "\"1mO0ricChifJ8mRlv_YmrJS-8DS4fc8WM\"")

        // Only include ARM architectures (Fire TV Stick) — drops APK size ~60%
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    
    // Leanback for TV UI
    implementation("androidx.leanback:leanback:1.0.0")
    
    // Glide for Images
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Lifecycle (for lifecycleScope in Activity)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Media3 (ExoPlayer) — unified v1.3.1
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")
    implementation("androidx.media3:media3-common:1.3.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.3.1")
    
    // Jsoup for Scraping
    implementation("org.jsoup:jsoup:1.17.2")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Room Database
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    
    // ---------- NEW DEPENDENCIES ----------
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // Local HTTP Server
    implementation("org.nanohttpd:nanohttpd:2.3.1")
    
    // P2P Streaming Magic (Stremio-like)
    implementation("com.github.se-bastiaan:TorrentStream-Android:master-SNAPSHOT")

    // LibVLC — native codec support for MKV, AC3, EAC3, DTS, HEVC, etc.
    implementation("org.videolan.android:libvlc-all:3.6.0")

    // ---------- COMPOSE FOR TV ----------
    // Compose BOM — single source of truth for Compose versions
    val composeBom = platform("androidx.compose:compose-bom:2024.02.02")
    implementation(composeBom)

    // Compose for TV
    implementation("androidx.tv:tv-foundation:1.0.0-alpha11")
    implementation("androidx.tv:tv-material:1.0.0")

    // Core Compose (versions pinned by BOM)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Coil — async image loading for Compose
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Debug tooling
    debugImplementation("androidx.compose.ui:ui-tooling")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.02"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

