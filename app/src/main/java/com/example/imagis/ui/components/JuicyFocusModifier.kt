// FILE_PATH: app/src/main/java/com/example/imagis/ui/components/JuicyFocusModifier.kt
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.imagis.ui.theme.NeonLime
import com.example.imagis.ui.theme.PureWhite

// ── JUICY BRUTALIST FOCUS ──────────────────────────────────
//
//  A TV-first focus modifier with heavy, elastic "juicy"
//  physics. Designed for D-Pad navigation on pure black
//  backgrounds.
//
//  States:
//   • UNFOCUSED — 1.0 scale, no border, no shadow
//   • FOCUSED   — 1.1 scale (spring snap), 4dp white border,
//                  hard-edged neon lime offset shadow
//   • PRESSED   — 0.95 scale (instant squash), border held
//

// -- Spring configs --

/** Heavy snap: high stiffness, medium damping → elastic punch. */
private val JuicySpring = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,  // 0.5 — visible overshoot
    stiffness    = Spring.StiffnessHigh,              // 10_000 — fast attack
)

/** Border color fades in fast but doesn't need bounce. */
private val BorderColorSpec = spring<Color>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness    = Spring.StiffnessMediumLow,
)

// -- Shadow constants --

/** The offset shadow shifts right and down by this amount. */
private val ShadowOffsetDp = 6.dp

/** Shadow color: Neon Lime, fully opaque, hard edge. */
private val ShadowColor = NeonLime

// -- Border constants --

private val FocusedBorderWidth = 4.dp
private val FocusedBorderColor = PureWhite
private val UnfocusedBorderColor = Color.Transparent

/**
 * A Compose TV focus modifier implementing 'Juicy Brutalism' physics.
 *
 * Attach this to any composable that participates in D-Pad focus
 * navigation. It handles focus + press states internally.
 *
 * ```kotlin
 * Box(
 *     modifier = Modifier
 *         .size(200.dp)
 *         .juicyBrutalistFocus()
 * ) { ... }
 * ```
 */
fun Modifier.juicyBrutalistFocus(): Modifier = composed {

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    // ── SCALE ──────────────────────────────────────────────
    // Focused → 1.1 with violent spring snap
    // Pressed → 0.95 instant squash (heavy tactile rubber)
    // Unfocused → 1.0 neutral
    val targetScale = when {
        isPressed -> 0.95f
        isFocused -> 1.1f
        else      -> 1.0f
    }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = if (isPressed) {
            // Instant squash — no spring, just snap
            tween(durationMillis = 50)
        } else {
            JuicySpring
        },
        label = "juicy_scale",
    )

    // ── BORDER COLOR ───────────────────────────────────────
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isFocused || isPressed) FocusedBorderColor else UnfocusedBorderColor,
        animationSpec = BorderColorSpec,
        label = "juicy_border",
    )

    // ── SHADOW OPACITY ─────────────────────────────────────
    val shadowAlpha by animateFloatAsState(
        targetValue = if (isFocused || isPressed) 1f else 0f,
        animationSpec = JuicySpring,
        label = "juicy_shadow",
    )

    val shadowOffsetPx = ShadowOffsetDp

    this
        // 1. Hard-edged offset shadow drawn BEHIND the content
        .drawBehind {
            if (shadowAlpha > 0.01f) {
                val offsetPx = shadowOffsetPx.toPx()
                drawRect(
                    color    = ShadowColor.copy(alpha = shadowAlpha),
                    topLeft  = Offset(offsetPx, offsetPx),
                    size     = Size(size.width, size.height),
                )
            }
        }
        // 2. Scale transform — spring snap or instant squash
        .scale(animatedScale)
        // 3. Thick solid border — white on focus, transparent off
        .border(
            width = FocusedBorderWidth,
            color = animatedBorderColor,
            shape = RectangleShape,  // 0dp corners — brutalist
        )
        // 4. Make focusable with our interaction source
        .focusable(interactionSource = interactionSource)
}
