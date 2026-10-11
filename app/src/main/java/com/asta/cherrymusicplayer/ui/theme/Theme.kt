package com.asta.cherrymusicplayer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CherryDarkColorScheme = darkColorScheme(
    primary = CherryPrimary,
    onPrimary = Color.White,
    primaryContainer = CherrySurface,
    onPrimaryContainer = CherryTextPrimary,
    secondary = CherryPrimaryDark,
    onSecondary = Color.White,
    secondaryContainer = CherrySurface,
    onSecondaryContainer = CherryTextSecondary,
    background = CherryBackground,
    onBackground = CherryTextPrimary,
    surface = CherrySurface,
    onSurface = CherryTextPrimary,
    surfaceVariant = CherryCardColor,
    onSurfaceVariant = CherryTextSecondary,
    outline = CherryBorder,
    outlineVariant = CherryBorder
)

@Composable
fun CherryMusicPlayerTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CherryDarkColorScheme,
        typography = Typography,
        content = content
    )
}
