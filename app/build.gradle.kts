plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.chatrealtime"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.chatrealtime"
        minSdk = 31
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
}

dependencies {
    // Implementacion de boom para compatibilidad de versiones
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    // Dependencia de RealTimeDatabase
    implementation("com.google.firebase:firebase-database")
    // Dependencia de Auth
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth")

    // Dependencias de MQTT
    implementation("com.github.hannesa2:paho.mqtt.android:3.3.5")
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}