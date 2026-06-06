package com.example.aulix.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Light color scheme ────────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary              = Cobalto,
    onPrimary            = Color.White,
    primaryContainer     = Cielo,
    onPrimaryContainer   = Tinta,

    secondary            = Cobre,
    onSecondary          = Color.White,
    secondaryContainer   = Arena,
    onSecondaryContainer = Tinta,

    background           = Lienzo,
    onBackground         = Tinta,

    surface              = SurfaceLight,
    onSurface            = Tinta,
    surfaceVariant       = Arena,
    onSurfaceVariant     = TintaMuted,

    outline              = BorderLight,
    outlineVariant       = Cielo,

    error                = StatusRed,
    onError              = Color.White,
    errorContainer       = StatusRedBg,
    onErrorContainer     = StatusRed,

    inverseSurface       = Tinta,
    inverseOnSurface     = TextOnDark,
)

// ── Dark color scheme ─────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary              = CobaltoDark,
    onPrimary            = TintaDark,
    primaryContainer     = CieloDark,
    onPrimaryContainer   = TextOnDark,

    secondary            = Cobre,
    onSecondary          = TextOnDark,
    secondaryContainer   = SurfaceVarDark,
    onSecondaryContainer = TextOnDark,

    background           = TintaDark,
    onBackground         = TextOnDark,

    surface              = SurfaceDark,
    onSurface            = TextOnDark,
    surfaceVariant       = SurfaceVarDark,
    onSurfaceVariant     = TextMutedDark,

    outline              = BorderDark,
    outlineVariant       = SurfaceVarDark,

    error                = Color(0xFFF87171),
    onError              = TintaDark,
    errorContainer       = Color(0xFF3D0A0A),
    onErrorContainer     = Color(0xFFF87171),

    inverseSurface       = Lienzo,
    inverseOnSurface     = Tinta,
)

// ── Colores de estado como CompositionLocal ───────────────────────────────────
data class AulixStatusColors(
    val green: Color,
    val greenBg: Color,
    val amber: Color,
    val amberBg: Color,
    val red: Color,
    val redBg: Color,
    val gray: Color,
    val grayBg: Color,
)

val LocalStatusColors = staticCompositionLocalOf {
    AulixStatusColors(
        green = StatusGreen, greenBg = StatusGreenBg,
        amber = StatusAmber, amberBg = StatusAmberBg,
        red = StatusRed, redBg = StatusRedBg,
        gray = StatusGray, grayBg = StatusGrayBg,
    )
}

@Composable
fun AulixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val statusColors = if (darkTheme) {
        AulixStatusColors(
            green = Color(0xFF4ADE80), greenBg = StatusGreenBgDk,
            amber = Color(0xFFFBBF24), amberBg = Color(0xFF2D1B00),
            red = Color(0xFFF87171), redBg = Color(0xFF3D0A0A),
            gray = Color(0xFF9CA3AF), grayBg = SurfaceVarDark,
        )
    } else {
        AulixStatusColors(
            green = StatusGreen, greenBg = StatusGreenBg,
            amber = StatusAmber, amberBg = StatusAmberBg,
            red = StatusRed, redBg = StatusRedBg,
            gray = StatusGray, grayBg = StatusGrayBg,
        )
    }

    CompositionLocalProvider(LocalStatusColors provides statusColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AulixTypography,
            content = content,
        )
    }
}

object AulixTheme {
    val statusColors: AulixStatusColors
        @Composable get() = LocalStatusColors.current
}
