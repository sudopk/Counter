package com.sudopk.counter.ui.theme

import android.os.Build
import android.view.Window
import android.view.WindowInsets
import androidx.compose.ui.graphics.Color

val Amber200 = Color(0xFFFFE082)
val Amber500 = Color(0xFFFFC107)
val Amber700 = Color(0xFFFFA000)
val AmberSecondary200 = Color(0xFFFFD740)

// See https://stackoverflow.com/a/79338465
fun Window.setStatusBarColorBySdkVersion(color: Int) {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15+
    this.decorView.setOnApplyWindowInsetsListener { view, insets ->
      val statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars())
      view.setBackgroundColor(color)

      // Adjust padding to avoid overlap
      view.setPadding(0, statusBarInsets.top, 0, 0)
      insets
    }
  } else {
    // For Android 14 and below
    this.statusBarColor = color
  }
}
