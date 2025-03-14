plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.compose.compiler)
}

android {
  compileSdk = 35
  namespace = "com.sudopk.counter"

  defaultConfig {
    minSdk = 24
    targetSdk = compileSdk
    versionCode = 4
    versionName = "1.3"

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
    // https://developer.android.com/build/jdks#source-compat
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }
  kotlinOptions {
    jvmTarget = "21"
  }
  buildFeatures {
    compose = true
  }
  buildToolsVersion = "35.0.0"
}

dependencies {
  implementation(libs.core.ktx)
  implementation(libs.preference.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.androidx.ui)
  implementation(libs.androidx.material)
  implementation(libs.androidx.ui.tooling)
  implementation(libs.lifecycle.runtime.ktx)
  implementation(libs.activity.compose)
  implementation(libs.kandroid)
  implementation(libs.androidx.material.icons.extended)

  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  androidTestImplementation(composeBom)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.espresso.core)
  androidTestImplementation(libs.androidx.ui.test.junit4)
}