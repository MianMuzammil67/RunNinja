plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.google.dagger.hilt.android)
    alias(libs.plugins.navigation.safe.args)
    id("kotlin-parcelize")


}

android {
    namespace = "com.example.runningtrakerapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.runningtrakerapp"
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
    kotlinOptions {
        jvmTarget = "1.8"
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
    implementation(libs.play.services.maps)
    implementation(libs.androidx.lifecycle.service)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    Room Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
//    Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

//    implementation (libs.androidx.activity.ktx)


//    Glide
    implementation(libs.glide)
//    Easy permission
    implementation (libs.easypermissions)
//    MP Android Chart
    implementation (libs.mpandroidchart)
//    Navigation graph
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation (libs.easypermissions)

    implementation (libs.play.services.location)
    implementation (libs.play.services.maps)


//    // Coroutines
//    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5'
//    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.5'

    val nav_version = "2.7.3"

    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

}