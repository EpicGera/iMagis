// FILE_PATH: app/src/main/java/com/example/imagis/ui/SplashActivity.kt
// ACTION: CREATE
// ---------------------------------------------------------
package com.example.imagis.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.imagis.ui.theme.FlixBlack
import com.example.imagis.ui.theme.FlixRed
import com.example.imagis.ui.theme.FlixTheme
import com.example.imagis.ui.theme.FlixWhite
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FlixTheme {
                SplashScreen(
                    onFinished = {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
                )
            }
        }
    }
}

// ── SPLASH SCREEN COMPOSABLE ───────────────────────────────

@Composable
private fun SplashScreen(
    onFinished: () -> Unit,
) {
    var showLogo by remember { mutableStateOf(false) }
    var showSubtitle by remember { mutableStateOf(false) }

    // Stagger the animations
    LaunchedEffect(Unit) {
        delay(400)
        showLogo = true
        delay(800)
        showSubtitle = true
        delay(2000)
        onFinished()
    }

    // Random trending backdrop URLs (static set for splash)
    val backdropPaths = listOf(
        "https://image.tmdb.org/t/p/w1280/zDgB8Bsc3xLVKjJMadQVkwO7lTi.jpg",
        "https://image.tmdb.org/t/p/w1280/qjGrUmKW78MCFG8PTVCp6SkOz4K.jpg",
        "https://image.tmdb.org/t/p/w1280/8b8R8l88Qje9dn9OE8PY05Nxl1X.jpg",
        "https://image.tmdb.org/t/p/w1280/yDHYTfA3R0jFYba16jBB1ef8oIt.jpg",
        "https://image.tmdb.org/t/p/w1280/xOMo8BRK7PfcJv9JCnx7s5hj0PX.jpg",
    )
    val selectedBackdrop = remember { backdropPaths.random() }

    val infiniteTransition = rememberInfiniteTransition(label = "splash")

    // Rotating ring
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
        ),
        label = "ring_rotation",
    )

    // Pulsing glow
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow_pulse",
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        // Background: poster backdrop with heavy gradient
        AsyncImage(
            model = selectedBackdrop,
            contentDescription = "Backdrop",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.3f),
        )

        // Dark gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            FlixBlack.copy(alpha = 0.5f),
                            FlixBlack.copy(alpha = 0.85f),
                            FlixBlack,
                        ),
                        center = Offset(0.5f, 0.5f),
                        radius = 1200f,
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Pulsing ring behind logo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .rotate(ringRotation)
                    .drawBehind {
                        // Outer arc
                        drawArc(
                            color = FlixRed.copy(alpha = 0.7f),
                            startAngle = 0f,
                            sweepAngle = 240f,
                            useCenter = false,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                        )
                        // Inner glow arc
                        drawArc(
                            color = FlixRed.copy(alpha = glowAlpha),
                            startAngle = 180f,
                            sweepAngle = 120f,
                            useCenter = false,
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round),
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                // Logo text with spring scale-in
                val logoScale by animateFloatAsState(
                    targetValue = if (showLogo) 1f else 0.5f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f),
                    label = "logo_scale"
                )
                val logoAlpha by animateFloatAsState(
                    targetValue = if (showLogo) 1f else 0f,
                    animationSpec = tween(600),
                    label = "logo_alpha"
                )
                Text(
                    text = "iM",
                    color = FlixWhite,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Black,
                    fontSize = 40.sp,
                    modifier = Modifier.scale(logoScale).alpha(logoAlpha)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Full title
            AnimatedVisibility(
                visible = showLogo,
                enter = fadeIn(tween(800)) + scaleIn(
                    animationSpec = spring(dampingRatio = 0.7f, stiffness = 150f),
                ),
            ) {
                Text(
                    text = "iMagis",
                    color = FlixWhite,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Black,
                    fontSize = 48.sp,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            AnimatedVisibility(
                visible = showSubtitle,
                enter = fadeIn(tween(600)),
            ) {
                Text(
                    text = "Your Cinema. Everywhere.",
                    color = FlixRed,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                )
            }
        }
    }
}
