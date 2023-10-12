plugins {
  id("com.android.application")
  id("kotlin-android")
}

val composeVersion = "1.5.3"

android {
  compileSdk = 34
  namespace = "com.sudopk.counter"

  defaultConfig {
    minSdk = 24
    targetSdk = compileSdk
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = composeVersion
  }
}

dependencies {
  implementation("androidx.core:core-ktx:1.12.0")
  implementation("androidx.preference:preference-ktx:1.2.1")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("com.google.android.material:material:1.10.0")
  implementation("androidx.compose.ui:ui:$composeVersion")
  implementation("androidx.compose.material:material:$composeVersion")
  implementation("androidx.compose.ui:ui-tooling:$composeVersion")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
  implementation("androidx.activity:activity-compose:1.8.0")
  implementation("com.github.sudopk:KAndroid:1.1.2")

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
  androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
}