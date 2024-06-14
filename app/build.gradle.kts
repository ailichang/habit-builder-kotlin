plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
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
}

dependencies {
    implementation ("androidx.core:core-ktx:1.13.1")
    testImplementation("junit:junit:4.13.2")
    testImplementation ("androidx.test:core:1.5.0")
    testImplementation ("androidx.test:core-ktx:1.5.0")
    testImplementation ("androidx.test.ext:junit:1.1.5")
    testImplementation ("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation ("androidx.arch.core:core-testing:2.2.0")
    testImplementation ("org.mockito:mockito-core:5.12.0")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:5.3.1")
    testImplementation ("org.robolectric:robolectric:4.12.2")
    testImplementation ("com.google.truth:truth:1.4.2")

    androidTestImplementation ("org.mockito.kotlin:mockito-kotlin:5.3.1")
    androidTestImplementation ("com.google.truth:truth:1.4.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("androidx.appcompat:appcompat:1.7.0")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

    val roomVersion = "2.6.1"
    implementation ("androidx.room:room-runtime:$roomVersion")
    // To use Kotlin Symbol Processing (KSP)
    ksp ("androidx.room:room-compiler:$roomVersion")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation ("androidx.room:room-ktx:$roomVersion")
    // optional - RxJava2 support for Room
    implementation ("androidx.room:room-rxjava2:$roomVersion")
    // optional - RxJava3 support for Room
    implementation ("androidx.room:room-rxjava3:$roomVersion")
    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation ("androidx.room:room-guava:$roomVersion")
    // optional - Test helpers
    testImplementation ("androidx.room:room-testing:$roomVersion")
    // optional - Paging 3 Integration
    implementation ("androidx.room:room-paging:$roomVersion")

    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    // For control over item selection of both touch and mouse driven selection
    implementation ("androidx.recyclerview:recyclerview-selection:1.2.0-alpha01")
    val lifecycleVersion = "2.8.2"
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    val fragmentVersion = "1.8.0"
    implementation ("androidx.fragment:fragment-ktx:$fragmentVersion")
    // Testing Fragments in Isolation
    debugImplementation ("androidx.fragment:fragment-testing:$fragmentVersion")
    implementation ("androidx.cardview:cardview:1.0.0")
    val preferenceVersion = "1.2.1"
    implementation ("androidx.preference:preference-ktx:$preferenceVersion")

    val daggerVersion = "2.51.1"
    implementation ("com.google.dagger:dagger:$daggerVersion")
    ksp ("com.google.dagger:dagger-compiler:$daggerVersion")
    ksp ("com.google.dagger:dagger-android-processor:$daggerVersion")
    implementation ("com.google.dagger:dagger-android:$daggerVersion")
    implementation ("com.google.dagger:dagger-android-support:$daggerVersion")

    val hiltVersion = "2.51.1"
    implementation ("com.google.dagger:hilt-android:$hiltVersion")
    ksp ("com.google.dagger:hilt-compiler:$hiltVersion")
    kspTest ("com.google.dagger:hilt-android-compiler:$hiltVersion")
    kspAndroidTest ("com.google.dagger:hilt-android-compiler:$hiltVersion")
    testImplementation ("com.google.dagger:hilt-android-testing:$hiltVersion")
    androidTestImplementation ("com.google.dagger:hilt-android-testing:$hiltVersion")
}