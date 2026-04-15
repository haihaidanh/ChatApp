plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.chat_app1204"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.chat_app1204"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }

    buildFeatures {
        compose = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)

    debugImplementation(libs.androidx.compose.ui.tooling)

    val pagingVersion = "3.3.5"

    // Core Paging Library
    implementation("androidx.paging:paging-runtime-ktx:$pagingVersion")

    // Jetpack Compose Integration (Essential for your UI)
    implementation("androidx.paging:paging-compose:$pagingVersion")

    // Room Integration (If you are doing Database + Network paging)
    implementation("androidx.room:room-paging:2.6.1")

    // Testing Paging
    testImplementation("androidx.paging:paging-common-ktx:$pagingVersion")

    val roomVersion = "2.6.1"

    // Room Runtime and KTX (Kotlin Extensions/Coroutines)
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    // Use KSP instead of KAPT for the compiler
    ksp("androidx.room:room-compiler:$roomVersion")

    // Optional: Paging 3 support
    // implementation("androidx.room:room-paging:$roomVersion")

    // Optional: Room Testing
    testImplementation("androidx.room:room-testing:$roomVersion")

    val hiltVersion = "2.52"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    ksp("com.google.dagger:hilt-android-compiler:$hiltVersion") // KSP Processor
    // Hilt Navigation Compose (Required for hiltViewModel() in Compose)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // --- RETROFIT ---
    val retrofitVersion = "2.11.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    // OkHttp & Logging (Essential for SDK 35 network security)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation(platform("com.google.firebase:firebase-bom:34.12.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("io.getstream:stream-webrtc-android:1.2.3")

    implementation("io.socket:socket.io-client:2.1.0") {
        exclude(group = "org.json", module = "json")
    }

    implementation("de.hdodenhof:circleimageview:3.1.0")
    //implementation("com.github.Aghajari:AXEmojiView:1.5.2")

}