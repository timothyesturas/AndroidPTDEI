package com.it.ptdei.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8AB4F8),
    onPrimary = Color.Black,

    secondary = Color(0xFF5C9DFF),
    tertiary = Color(0xFF3D8BFF),

    background = Color(0xFF0F1115),
    onBackground = Color.White,

    surface = Color(0xFF1A1C1E),
    onSurface = Color.White,

    error = Color(0xFFB3261E)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3D8BFF),
    onPrimary = Color.White,

    secondary = Color(0xFF5C9DFF),
    onSecondary = Color.White,

    tertiary = Color(0xFF8AB4F8),

    background = Color(0xFFF6F8FC),
    onBackground = Color.Black,

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),

    error = Color(0xFFB3261E)
)

@Composable
fun PTDEITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}