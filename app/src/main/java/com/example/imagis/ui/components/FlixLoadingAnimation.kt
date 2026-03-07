// FILE_PATH: app/src/main/java/com/example/imagis/ui/components/FlixLoadingAnimation.kt
// ACTION: CREATE
// ---------------------------------------------------------
package com.example.imagis.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.imagis.ui.theme.FlixBlack
import com.example.imagis.ui.theme.FlixCardSurface
import com.example.imagis.ui.theme.FlixRed
import com.example.imagis.ui.theme.FlixWhite

// ── SHIMMER POSTER CARD ────────────────────────────────────
// Placeholder card with diagonal shimmer sweep.

@Composable
fun ShimmerPosterCard(
    modifier: Modifier = Modifier,
    cardWidth: Dp = 160.dp,
    cardHeight: Dp = 240.dp,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue  = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_offset",
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            FlixCardSurface,
            FlixCardSurface.copy(alpha = 0.5f),
            Color(0xFF2A2A4A),
            FlixCardSurface.copy(alpha = 0.5f),
            FlixCardSurface,
        ),
        start = Offset(shimmerOffset * 300f, 0f),
        end   = Offset(shimmerOffset * 300f + 300f, 300f),
    )

    Column(
        modifier = modifier.width(cardWidth),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Poster placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .clip(RoundedCornerShape(8.dp))
                .background(shimmerBrush)
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Title placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(shimmerBrush)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(10.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(shimmerBrush)
        )
    }
}

// ── PULSING FLIX LOADER ────────────────────────────────────
// Full-screen center loader with rotating ring.

@Composable
fun PulsingFlixLoader(
    modifier: Modifier = Modifier,
    message: String = "Loading...",
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loader")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
        ),
        label = "rotation",
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Rotating ring
        Box(
            modifier = Modifier
                .size(64.dp)
                .rotate(rotation)
                .drawBehind {
                    drawArc(
                        color      = FlixRed,
                        startAngle = 0f,
                        sweepAngle = 270f,
                        useCenter  = false,
                        style      = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            // Inner dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(FlixRed.copy(alpha = pulseAlpha), CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = message,
            color = FlixWhite.copy(alpha = pulseAlpha),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
        )
    }
}
