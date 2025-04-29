plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    alias(libs.plugins.navigation.safeargs.kotlin)
}

android {
    namespace = "com.example.androidvirtualcollections"
    compileSdkVersion(rootProject.extra["compileSdkVersion"] as Int)

    defaultConfig {
        applicationId = "com.example.androidvirtualcollections"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("com.google.code.gson:gson:2.13.1")
    implementation(libs.androidx.recyclerview)
    implementation(libs.gson)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}