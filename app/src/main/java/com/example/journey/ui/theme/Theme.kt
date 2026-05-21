package com.example.journey.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ForestGreen,
    onPrimary = DarkCharcoal,
    secondary = WarmSage,
    onSecondary = Color.White,
    background = WarmLinen,
    onBackground = TextPrimary,
    surface = CardLinen,
    onSurface = TextPrimary,
    surfaceVariant = LightSage,
    onSurfaceVariant = TextSecondary,
    outline = BorderLinen
)

private val LightColorScheme = darkColorScheme(
    primary = ForestGreen,
    onPrimary = DarkCharcoal,
    secondary = WarmSage,
    onSecondary = Color.White,
    background = WarmLinen,
    onBackground = TextPrimary,
    surface = CardLinen,
    onSurface = TextPrimary,
    surfaceVariant = LightSage,
    onSurfaceVariant = TextSecondary,
    outline = BorderLinen
)

@Composable
fun JOURneyTheme(
    darkTheme: Boolean = true, // Force dark mode as default
    content: @Composable () -> Unit
) {
    // Always enforce the dark color scheme for the requested UI style
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}