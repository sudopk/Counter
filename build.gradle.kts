// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
  rootProject.extra["kotlinVersion"] = "1.5.10"
  val kotlinVersion = rootProject.extra["kotlinVersion"] as String

  repositories {
    google()
    mavenCentral()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:7.0.0-beta01")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")

    // NOTE: Do not place your application dependencies here; they belong
    // in the individual module build.gradle.kts files
  }
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}