package com.sudopk.counter.ui.theme

import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

private val AmberDarkColorPalette = darkColors(
  primary = Amber200,
  primaryVariant = Amber700,
  secondary = AmberSecondary200
)

private val AmberLightColorPalette = lightColors(
  primary = Amber500,
  primaryVariant = Amber700,
  secondary = AmberSecondary200
)


@Composable
fun CounterTheme(
  window: Window,
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable() () -> Unit
) {
  val colors = if (darkTheme) {
    AmberDarkColorPalette
  } else {
    AmberLightColorPalette
  }

  MaterialTheme(
    colors = colors,
    typography = Typography,
    shapes = Shapes,
  ) {
    window.statusBarColor = MaterialTheme.colors.statusBar.toArgb()
    content()
  }
}

val Colors.statusBar: Color get() = if (isLight) primaryVariant else surface
