plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.example.wisp.data.location"
    compileSdk = 34

    flavorDimensions += "platform"
    productFlavors {
        create("android") {
            dimension = "platform"
        }
        
        create("quest") {
            dimension = "platform"
        }
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        
        create("staging") {
            initWith(getByName("release"))
            isMinifyEnabled = false
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":domain"))

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Android flavor dependencies
    "androidImplementation"("com.google.android.gms:play-services-location:21.0.1")
    "androidImplementation"("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    
    // Quest flavor dependencies (IP geolocation)
    "questImplementation"(libs.retrofit)
    "questImplementation"(libs.retrofit.kotlinx.serialization)
    "questImplementation"(libs.okhttp)
    "questImplementation"(libs.kotlinx.serialization.json)

    // Common dependencies
    implementation("androidx.core:core-ktx:1.12.0")
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.22")
    testImplementation("org.mockito:mockito-core:5.1.1")
    testImplementation("org.mockito:mockito-junit-jupiter:5.1.1")
    testImplementation("org.robolectric:robolectric:4.10.3")
    testImplementation("androidx.test:core:1.5.0")
}

