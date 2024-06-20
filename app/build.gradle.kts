plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.parcelize)
}

android {
    namespace ="com.habitbuilder"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.habitbuilder"
        minSdk = 29
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility =  JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    testImplementation (libs.androidx.test.core)
    testImplementation (libs.androidx.test.core.ktx)
    testImplementation (libs.androidx.junit)
    testImplementation (libs.androidx.junit.ktx)
    testImplementation (libs.androidx.arch.core.testing)
    testImplementation (libs.mockito.core)
    testImplementation (libs.mockito.kotlin)
    testImplementation (libs.robolectric)
    testImplementation (libs.truth)
    androidTestImplementation (libs.mockito.kotlin)
    androidTestImplementation (libs.truth)
    androidTestImplementation (libs.androidx.junit)
    androidTestImplementation (libs.androidx.arch.core.testing)
    androidTestImplementation (libs.androidx.espresso.core)

    implementation (libs.androidx.appcompat)
    implementation (libs.material)
    implementation (libs.androidx.constraintlayout)
    implementation (libs.kotlinx.coroutines.android)
    testImplementation (libs.kotlinx.coroutines.test)
    androidTestImplementation (libs.kotlinx.coroutines.test)

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.runtime.rxjava2)

    implementation (libs.androidx.room.runtime)
    ksp (libs.androidx.room.compiler)
    implementation (libs.androidx.room.ktx)
    implementation (libs.androidx.room.rxjava2)
    implementation (libs.androidx.room.rxjava3)
    implementation (libs.androidx.room.guava)
    testImplementation (libs.androidx.room.testing)
    implementation (libs.androidx.room.paging)
    implementation (libs.androidx.recyclerview)
    implementation (libs.androidx.recyclerview.selection)
    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.lifecycle.livedata.ktx)
    implementation (libs.androidx.fragment.ktx)
    debugImplementation (libs.androidx.fragment.testing)
    implementation (libs.androidx.cardview)
    implementation (libs.androidx.preference.ktx)

    implementation (libs.dagger)
    ksp (libs.dagger.compiler)
    ksp (libs.dagger.android.processor)
    implementation (libs.dagger.android)
    implementation (libs.dagger.android.support)
    implementation (libs.hilt.android)
    ksp (libs.hilt.compiler)
    kspTest (libs.hilt.android.compiler)
    kspAndroidTest (libs.hilt.android.compiler)
    testImplementation (libs.hilt.android.testing)
    androidTestImplementation (libs.hilt.android.testing)
}