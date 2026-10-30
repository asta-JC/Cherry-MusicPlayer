package com.asta.cherrymusicplayer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.asta.cherrymusicplayer.data.preferences.ThemeMode

private val LightColors = lightColorScheme(
    primary = Color(0xFFAF294A), onPrimary = Color.White,
    primaryContainer = Color(0xFFF9DEE5), onPrimaryContainer = Color(0xFF652039),
    secondary = Color(0xFF78616B), onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1E7E7), onSecondaryContainer = Color(0xFF422731),
    tertiary = Color(0xFF526B4E), onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD5E8CE), onTertiaryContainer = Color(0xFF263E24),
    surfaceDim = Color(0xFFE5DAD8), surfaceBright = Color(0xFFFFFCFA),
    surfaceContainerLowest = Color.White, surfaceContainerLow = Color(0xFFFCF5F2),
    surfaceContainer = Color(0xFFF8F0ED), surfaceContainerHigh = Color(0xFFF1E7E7), surfaceContainerHighest = Color(0xFFEDE1E3),
    background = Color(0xFFFCF8F5), onBackground = Color(0xFF422731),
    surface = Color(0xFFFFFCFA), onSurface = Color(0xFF422731),
    surfaceVariant = Color(0xFFF1E7E7), onSurfaceVariant = Color(0xFF78616B),
    outline = Color(0xFF927780), outlineVariant = Color(0xFFE9DDDF)
)
private val DarkColors = darkColorScheme(
    primary = Color(0xFFFF9DB5), onPrimary = Color(0xFF3B1528),
    primaryContainer = Color(0xFF502A3B), onPrimaryContainer = Color(0xFFFFD9E3),
    secondary = Color(0xFFDDBDC9), onSecondary = Color(0xFF3B2932),
    secondaryContainer = Color(0xFF392A36), onSecondaryContainer = Color(0xFFF9E8EE),
    tertiary = Color(0xFFB7D0AC), onTertiary = Color(0xFF263E24),
    tertiaryContainer = Color(0xFF3C5338), onTertiaryContainer = Color(0xFFD5E8CE),
    surfaceDim = Color(0xFF1C151D), surfaceBright = Color(0xFF40353F),
    surfaceContainerLowest = Color(0xFF160F17), surfaceContainerLow = Color(0xFF231A24),
    surfaceContainer = Color(0xFF2A202B), surfaceContainerHigh = Color(0xFF332936), surfaceContainerHighest = Color(0xFF3D3340),
    background = Color(0xFF1C151D), onBackground = Color(0xFFF9E8EE),
    surface = Color(0xFF261D27), onSurface = Color(0xFFF9E8EE),
    surfaceVariant = Color(0xFF392A36), onSurfaceVariant = Color(0xFFCAB6C0),
    outline = Color(0xFF9B828F), outlineVariant = Color(0xFF4B3845)
)

@Composable
fun CherryMusicPlayerTheme(themeMode: ThemeMode = ThemeMode.SYSTEM, content: @Composable () -> Unit) {
    val dark = themeMode == ThemeMode.DARK || (themeMode == ThemeMode.SYSTEM && isSystemInDarkTheme())
    MaterialTheme(colorScheme = if (dark) DarkColors else LightColors, typography = Typography, content = content)
}
