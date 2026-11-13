package uz.yechim.app.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = YechimPurple,
    onPrimary = SurfaceLight,
    primaryContainer = YechimPurpleLight,
    onPrimaryContainer = YechimPurpleDark,
    secondary = YechimIndigo,
    onSecondary = SurfaceLight,
    tertiary = VibrantGold,
    background = BackgroundLight,
    onBackground = Color.Black,
    surface = SurfaceLight,
    onSurface = Color.Black,
    surfaceVariant = YechimPurpleLight,
    onSurfaceVariant = Color(0xFF1F2937),
    error = ErrorRed,
    outline = CardBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = YechimPurpleMid,
    onPrimary = SurfaceLight,
    primaryContainer = YechimPurpleDark,
    onPrimaryContainer = YechimPurpleLight,
    secondary = YechimIndigo,
    onSecondary = SurfaceLight,
    tertiary = VibrantGold,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = TextSecondaryDark,
    error = ErrorRed,
    outline = CardBorder
)

@Composable
fun YechimUzTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
