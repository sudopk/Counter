package com.sudopk.counter.ui.theme

import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorPalette = darkColorScheme()

private val LightColorPalette = lightColorScheme()

@Composable
fun CounterTheme(
    window: Window, darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(colorScheme = colorScheme) {
        // See https://stackoverflow.com/questions/76731368/how-to-remove-status-bar-in-jetpack-compose
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                WindowCompat.getInsetsController(
                    window, view
                ).isAppearanceLightStatusBars = !darkTheme
            }
        }

        content()
    }
}
