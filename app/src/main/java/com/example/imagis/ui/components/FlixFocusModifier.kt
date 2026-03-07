// FILE_PATH: app/src/main/java/com/example/imagis/ui/components/FlixFocusModifier.kt
// ACTION: CREATE
// ---------------------------------------------------------
package com.example.imagis.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.imagis.ui.theme.FlixRed

// ── FLIX FOCUS MODIFIER ────────────────────────────────────
//
//  Cinematic focus for TV D-Pad navigation.
//  Subtle, elegant — warm red glow on focus, gentle scale.
//
//  States:
//   • UNFOCUSED — 1.0 scale, no border, no glow
//   • FOCUSED   — 1.05 scale (smooth spring), 2dp red border, ambient glow
//   • PRESSED   — 0.97 scale (quick squash), glow held
//

private val FlixSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioLowBouncy,   // 0.75 — minimal overshoot
    stiffness    = Spring.StiffnessMedium,           // smooth cinema feel
)

private val FlixBorderColorSpec = spring<Color>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness    = Spring.StiffnessMediumLow,
)

private val FocusedBorderWidth = 2.dp
private val FocusBorderColor   = FlixRed
private val UnfocusedBorderColor = Color.Transparent
private val GlowColor = FlixRed.copy(alpha = 0.35f)

/**
 * Cinematic focus modifier for flix-style TV navigation.
 *
 * Attach to any composable in D-Pad focus flow.
 * Provides warm red glow, gentle scale, and squash on press.
 */
fun Modifier.flixFocus(): Modifier = composed {

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val targetScale = when {
        isPressed -> 0.97f
        isFocused -> 1.05f
        else      -> 1.0f
    }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = if (isPressed) tween(durationMillis = 80) else FlixSpring,
        label = "flix_scale",
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isFocused || isPressed) FocusBorderColor else UnfocusedBorderColor,
        animationSpec = FlixBorderColorSpec,
        label = "flix_border",
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (isFocused || isPressed) 1f else 0f,
        animationSpec = FlixSpring,
        label = "flix_glow",
    )

    this
        // Ambient glow behind content
        .drawBehind {
            if (glowAlpha > 0.01f) {
                val spread = 8.dp.toPx()
                drawRoundRect(
                    color        = GlowColor.copy(alpha = GlowColor.alpha * glowAlpha),
                    topLeft      = Offset(-spread, -spread),
                    size         = Size(size.width + spread * 2, size.height + spread * 2),
                    cornerRadius = CornerRadius(12.dp.toPx()),
                )
            }
        }
        .scale(animatedScale)
        .border(
            width = FocusedBorderWidth,
            color = animatedBorderColor,
            shape = RoundedCornerShape(8.dp),
        )
        .focusable(interactionSource = interactionSource)
}
