// FILE_PATH: app/src/main/java/com/example/imagis/ui/theme/BrutalistTheme.kt
// ACTION: CREATE
// ---------------------------------------------------------
package com.example.imagis.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── COLOR PALETTE ──────────────────────────────────────────
// Neo-Brutalist: no soft grays, no gradients, no compromise.

val DeepMatteBlack  = Color(0xFF000000)
val PureWhite       = Color(0xFFFFFFFF)
val NeonLime        = Color(0xFFBFFF00)
val ElectricMagenta = Color(0xFFFF00FF)

private val BrutalistColorScheme = darkColorScheme(
    primary            = NeonLime,
    onPrimary          = DeepMatteBlack,
    primaryContainer   = NeonLime,
    onPrimaryContainer = DeepMatteBlack,

    secondary            = ElectricMagenta,
    onSecondary          = DeepMatteBlack,
    secondaryContainer   = ElectricMagenta,
    onSecondaryContainer = DeepMatteBlack,

    tertiary            = ElectricMagenta,
    onTertiary          = DeepMatteBlack,
    tertiaryContainer   = ElectricMagenta,
    onTertiaryContainer = DeepMatteBlack,

    background   = DeepMatteBlack,
    onBackground = PureWhite,

    surface   = DeepMatteBlack,
    onSurface = PureWhite,

    surfaceVariant   = DeepMatteBlack,
    onSurfaceVariant = PureWhite,

    error   = ElectricMagenta,
    onError = DeepMatteBlack,

    outline        = NeonLime,
    outlineVariant = ElectricMagenta,

    inverseSurface   = PureWhite,
    inverseOnSurface = DeepMatteBlack,
    inversePrimary   = DeepMatteBlack,

    scrim = DeepMatteBlack,
)

// ── TYPOGRAPHY ─────────────────────────────────────────────
// Monospace everything. Heavy headings. No subtlety.

private val BrutalistTypography = Typography(
    displayLarge = TextStyle(
        fontFamily  = FontFamily.Monospace,
        fontWeight  = FontWeight.Black,
        fontSize    = 57.sp,
        lineHeight  = 64.sp,
        letterSpacing = (-0.25).sp,
        color       = PureWhite,
    ),
    displayMedium = TextStyle(
        fontFamily  = FontFamily.Monospace,
        fontWeight  = FontWeight.Black,
        fontSize    = 45.sp,
        lineHeight  = 52.sp,
        letterSpacing = 0.sp,
        color       = PureWhite,
    ),
    displaySmall = TextStyle(
        fontFamily  = FontFamily.Monospace,
        fontWeight  = FontWeight.Black,
        fontSize    = 36.sp,
        lineHeight  = 44.sp,
        letterSpacing = 0.sp,
        color       = PureWhite,
    ),
    headlineLarge = TextStyle(
        fontFamily  = FontFamily.Monospace,
        fontWeight  = FontWeight.Black,
        fontSize    = 32.sp,
        lineHeight  = 40.sp,
        letterSpacing = 0.sp,
        color       = PureWhite,
    ),
    headlineMedium = TextStyle(
        fontFamily  = FontFamily.Monospace,
        fontWeight  = FontWeight.Black,
        fontSize    = 28.sp,
        lineHeight  = 36.sp,
        letterSpacing = 0.sp,
        color       = PureWhite,
    ),
    headlineSmall = TextStyle(
        fontFamily  = FontFamily.Monospace,
        fontWeight  = FontWeight.Bold,
        fontSize    = 24.sp,
        lineHeight  = 32.sp,
        letterSpacing = 0.sp,
        color       = PureWhite,
    ),
    titleLarge = TextStyle(
        fontFamily  = FontFamily.Monospace,
        fontWeight  = FontWeight.Bold,
        fontSize    = 22.sp,
        lineHeight  = 28.sp,
        letterSpacing = 0.sp,
        color       = PureWhite,
    ),
    titleMedium = TextStyle(
        fontFamily  = FontFamily.Monospace,
        fontWeight  = FontWeight.Bold,
        fontSize    = 16.sp,
        lineHeight  = 24.sp,
        letterSpacing = 0.15.sp,
        color       = PureWhite,
    ),
    titleSmall = TextStyle(
        fontFamily  = FontFamily.Monospace,
        fontWeight  = FontWeight.Bold,
        fontSize    = 14.sp,
        lineHeight  = 20.sp,
        letterSpacing = 0.1.sp,
        color       = PureWhite,
    ),
    bodyLarge = TextStyle(
        fontFamily  = FontFamily.Monospace,
        fontWeight  = FontWeight.Bold,
        fontSize    = 16.sp,
        lineHeight  = 24.sp,
        letterSpacing = 0.5.sp,
        color       = PureWhite,
    ),
    bodyMedium = TextStyle(
        fontFamily  = FontFamily.Monospace,
        fontWeight  = FontWeight.Bold,
        fontSize    = 14.sp,
        lineHeight  = 20.sp,
        letterSpacing = 0.25.sp,
        color       = PureWhite,
    ),
    bodySmall = TextStyle(
        fontFamily  = FontFamily.Monospace,
        fontWeight  = FontWeight.Normal,
        fontSize    = 12.sp,
        lineHeight  = 16.sp,
        letterSpacing = 0.4.sp,
        color       = PureWhite,
    ),
    labelLarge = TextStyle(
        fontFamily  = FontFamily.Monospace,
        fontWeight  = FontWeight.Bold,
        fontSize    = 14.sp,
        lineHeight  = 20.sp,
        letterSpacing = 0.1.sp,
        color       = PureWhite,
    ),
    labelMedium = TextStyle(
        fontFamily  = FontFamily.Monospace,
        fontWeight  = FontWeight.Bold,
        fontSize    = 12.sp,
        lineHeight  = 16.sp,
        letterSpacing = 0.5.sp,
        color       = PureWhite,
    ),
    labelSmall = TextStyle(
        fontFamily  = FontFamily.Monospace,
        fontWeight  = FontWeight.Bold,
        fontSize    = 11.sp,
        lineHeight  = 16.sp,
        letterSpacing = 0.5.sp,
        color       = PureWhite,
    ),
)

// ── SHAPES ─────────────────────────────────────────────────
// ALL corners: 0dp. Perfect 90-degree right angles. Always.

private val BrutalistShapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),
    small      = RoundedCornerShape(0.dp),
    medium     = RoundedCornerShape(0.dp),
    large      = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(0.dp),
)

// ── THEME COMPOSABLE ───────────────────────────────────────

@Composable
fun BrutalistTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BrutalistColorScheme,
        typography  = BrutalistTypography,
        shapes      = BrutalistShapes,
        content     = content,
    )
}
