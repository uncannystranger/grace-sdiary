package com.grace.sdiary.ui.theme

import android.app.Activity
import androidx.compose.animation.core.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Color.Black,
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,
    secondary = Teal,
    onSecondary = Color.White,
    secondaryContainer = TealDark,
    onSecondaryContainer = TealLight,
    tertiary = Coral,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = TextHigh,
    surface = DarkSurface,
    onSurface = TextHigh,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMid,
    outline = GlassBorder,
    outlineVariant = Color(0x22FFFFFF),
    inverseSurface = LightSurface,
    inverseOnSurface = LightTextHigh,
    error = Error,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Gold,
    onPrimary = Color.White,
    primaryContainer = GoldLight,
    onPrimaryContainer = GoldDark,
    secondary = Teal,
    onSecondary = Color.White,
    secondaryContainer = TealLight,
    onSecondaryContainer = TealDark,
    tertiary = Coral,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextHigh,
    surface = LightSurface,
    onSurface = LightTextHigh,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextMid,
    outline = LightGlassBorder,
    outlineVariant = Color(0x0A000000),
    inverseSurface = DarkSurface,
    inverseOnSurface = TextHigh,
    error = Error,
    onError = Color.White
)

private val LocalGlassColors = staticCompositionLocalOf {
    GlassColors(dark = false)
}

data class GlassColors(
    val dark: Boolean,
    val glassBackground: Color = if (dark) Color(0x1AFFFFFF) else Color(0x08FFFFFF),
    val glassBorder: Color = if (dark) Color(0x22FFFFFF) else Color(0x10FFFFFF),
    val glassBgStrong: Color = if (dark) Color(0x28FFFFFF) else Color(0x05FFFFFF),
    val cardBackground: Color = if (dark) DarkCard else LightCard,
    val textHigh: Color = if (dark) TextHigh else LightTextHigh,
    val textMid: Color = if (dark) TextMid else LightTextMid,
    val textLow: Color = if (dark) TextLow else LightTextLow
) {
    companion object {
        fun forDark(isDark: Boolean) = if (isDark) GlassColors(dark = true) else GlassColors(
            dark = false,
            glassBackground = Color(0x08FFFFFF),
            glassBorder = Color(0x10FFFFFF),
            glassBgStrong = Color(0x05FFFFFF),
            cardBackground = LightCard,
            textHigh = LightTextHigh,
            textMid = LightTextMid,
            textLow = LightTextLow
        )
    }
}

object GraceTheme {
    val glassColors: GlassColors
        @Composable get() = LocalGlassColors.current
}

@Composable
fun GraceDiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val glassColors = remember(darkTheme) { GlassColors.forDark(darkTheme) }

    CompositionLocalProvider(LocalGlassColors provides glassColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}
