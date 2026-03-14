// FILE_PATH: app/src/main/java/com/epicgera/vtrae/ui/theme/FlixTheme.kt
// ACTION: CREATE
// ---------------------------------------------------------
package com.epicgera.vtrae.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import com.epicgera.vtrae.ui.components.VtrToastHost
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── FLIX COLOR PALETTE ─────────────────────────────────────
// Warm cinema palette inspired by premium streaming services.

val FlixBlack       = Color(0xFF0D0D0D)   // Warm soot background
val FlixSurface     = Color(0xFF1A1A2E)   // Midnight navy cards
val FlixCardSurface = Color(0xFF16213E)   // Deep blue card bg
val FlixRed         = Color(0xFFE50914)   // Iconic flix red
val FlixAmber       = Color(0xFFE87C03)   // Warm amber accent
val FlixGold        = Color(0xFFFFD700)   // Rating star gold
val FlixWhite       = Color(0xFFE5E5E5)   // Soft white text
val FlixGray        = Color(0xFF808080)   // Muted label gray
val FlixDimBlack    = Color(0xCC0D0D0D)   // 80% black overlay

private val FlixColorScheme = darkColorScheme(
    primary            = FlixRed,
    onPrimary          = FlixWhite,
    primaryContainer   = FlixRed,
    onPrimaryContainer = FlixWhite,

    secondary            = FlixAmber,
    onSecondary          = FlixBlack,
    secondaryContainer   = FlixAmber,
    onSecondaryContainer = FlixBlack,

    tertiary            = FlixGold,
    onTertiary          = FlixBlack,
    tertiaryContainer   = FlixGold,
    onTertiaryContainer = FlixBlack,

    background   = FlixBlack,
    onBackground = FlixWhite,

    surface   = FlixSurface,
    onSurface = FlixWhite,

    surfaceVariant   = FlixCardSurface,
    onSurfaceVariant = FlixWhite,

    error   = FlixRed,
    onError = FlixWhite,

    outline        = FlixRed,
    outlineVariant = FlixAmber,

    inverseSurface   = FlixWhite,
    inverseOnSurface = FlixBlack,
    inversePrimary   = FlixBlack,

    scrim = FlixBlack,
)

// ── TYPOGRAPHY ─────────────────────────────────────────────
// Clean sans-serif. Readable on TV from the couch.

private val FlixTypography = Typography(
    displayLarge = TextStyle(
        fontFamily  = FontFamily.SansSerif,
        fontWeight  = FontWeight.Black,
        fontSize    = 57.sp,
        lineHeight  = 64.sp,
        letterSpacing = (-0.25).sp,
        color       = FlixWhite,
    ),
    displayMedium = TextStyle(
        fontFamily  = FontFamily.SansSerif,
        fontWeight  = FontWeight.Black,
        fontSize    = 45.sp,
        lineHeight  = 52.sp,
        color       = FlixWhite,
    ),
    displaySmall = TextStyle(
        fontFamily  = FontFamily.SansSerif,
        fontWeight  = FontWeight.Bold,
        fontSize    = 36.sp,
        lineHeight  = 44.sp,
        color       = FlixWhite,
    ),
    headlineLarge = TextStyle(
        fontFamily  = FontFamily.SansSerif,
        fontWeight  = FontWeight.Bold,
        fontSize    = 32.sp,
        lineHeight  = 40.sp,
        color       = FlixWhite,
    ),
    headlineMedium = TextStyle(
        fontFamily  = FontFamily.SansSerif,
        fontWeight  = FontWeight.Bold,
        fontSize    = 28.sp,
        lineHeight  = 36.sp,
        color       = FlixWhite,
    ),
    headlineSmall = TextStyle(
        fontFamily  = FontFamily.SansSerif,
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 24.sp,
        lineHeight  = 32.sp,
        color       = FlixWhite,
    ),
    titleLarge = TextStyle(
        fontFamily  = FontFamily.SansSerif,
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 22.sp,
        lineHeight  = 28.sp,
        color       = FlixWhite,
    ),
    titleMedium = TextStyle(
        fontFamily  = FontFamily.SansSerif,
        fontWeight  = FontWeight.Medium,
        fontSize    = 16.sp,
        lineHeight  = 24.sp,
        letterSpacing = 0.15.sp,
        color       = FlixWhite,
    ),
    titleSmall = TextStyle(
        fontFamily  = FontFamily.SansSerif,
        fontWeight  = FontWeight.Medium,
        fontSize    = 14.sp,
        lineHeight  = 20.sp,
        letterSpacing = 0.1.sp,
        color       = FlixWhite,
    ),
    bodyLarge = TextStyle(
        fontFamily  = FontFamily.SansSerif,
        fontWeight  = FontWeight.Normal,
        fontSize    = 16.sp,
        lineHeight  = 24.sp,
        letterSpacing = 0.5.sp,
        color       = FlixWhite,
    ),
    bodyMedium = TextStyle(
        fontFamily  = FontFamily.SansSerif,
        fontWeight  = FontWeight.Normal,
        fontSize    = 14.sp,
        lineHeight  = 20.sp,
        letterSpacing = 0.25.sp,
        color       = FlixWhite,
    ),
    bodySmall = TextStyle(
        fontFamily  = FontFamily.SansSerif,
        fontWeight  = FontWeight.Normal,
        fontSize    = 12.sp,
        lineHeight  = 16.sp,
        letterSpacing = 0.4.sp,
        color       = FlixGray,
    ),
    labelLarge = TextStyle(
        fontFamily  = FontFamily.SansSerif,
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 14.sp,
        lineHeight  = 20.sp,
        letterSpacing = 0.1.sp,
        color       = FlixWhite,
    ),
    labelMedium = TextStyle(
        fontFamily  = FontFamily.SansSerif,
        fontWeight  = FontWeight.Medium,
        fontSize    = 12.sp,
        lineHeight  = 16.sp,
        letterSpacing = 0.5.sp,
        color       = FlixWhite,
    ),
    labelSmall = TextStyle(
        fontFamily  = FontFamily.SansSerif,
        fontWeight  = FontWeight.Medium,
        fontSize    = 11.sp,
        lineHeight  = 16.sp,
        letterSpacing = 0.5.sp,
        color       = FlixGray,
    ),
)

// ── SHAPES ─────────────────────────────────────────────────
// Soft rounded corners — cinema-friendly.

private val FlixShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(8.dp),
    medium     = RoundedCornerShape(12.dp),
    large      = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

// ── THEME COMPOSABLE ───────────────────────────────────────

@Composable
fun FlixTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FlixColorScheme,
        typography  = FlixTypography,
        shapes      = FlixShapes,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
            VtrToastHost()
        }
    }
}

