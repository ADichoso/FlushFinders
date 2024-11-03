plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.mobdeve.s18.banyoboyz.flushfinders"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mobdeve.s18.banyoboyz.flushfinders"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    sourceSets {
        getByName("main") {
            res {
                srcDirs(
                    "src\\main\\res\\layouts\\main-menu-views",
                    "src\\main\\res\\layouts\\user-mode-views",
                    "src\\main\\res\\layouts\\mod-mode-views",
                    "src\\main\\res\\layouts\\admin-mode-views")
            }
        }
    }
}

dependencies {
    // Firebase Realtime Database
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")
    implementation("org.mindrot:jbcrypt:0.4")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}