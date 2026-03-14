// FILE_PATH: app/src/main/java/com/epicgera/vtrae/ui/SplashActivity.kt
// ACTION: OVERWRITE
// ---------------------------------------------------------
package com.epicgera.vtrae.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
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
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.epicgera.vtrae.R
import com.epicgera.vtrae.ui.theme.FlixBlack
import com.epicgera.vtrae.ui.theme.FlixRed
import com.epicgera.vtrae.ui.theme.FlixTheme
import com.epicgera.vtrae.ui.theme.FlixWhite
import kotlinx.coroutines.delay
import androidx.compose.ui.res.stringResource
import kotlin.math.sin
import kotlin.random.Random

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

// ── CINEMATIC SPLASH COMPOSABLE ────────────────────────────

@Composable
private fun SplashScreen(
    onFinished: () -> Unit,
) {
    // ── PHASE CONTROL ──
    var phaseBackdrop by remember { mutableStateOf(false) }
    var phaseScanLine by remember { mutableStateOf(false) }
    var phaseLogoSlam by remember { mutableStateOf(false) }
    var phaseTitle by remember { mutableStateOf(false) }
    var phaseSubtitle by remember { mutableStateOf(false) }
    var phaseEmbers by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        phaseBackdrop = true     // Backdrop fades in + zoom
        delay(600)
        phaseScanLine = true     // Red scan-line sweeps
        delay(400)
        phaseLogoSlam = true     // Logo slams into center
        phaseEmbers = true       // Embers start floating
        delay(600)
        phaseTitle = true        // Title slides up
        delay(400)
        phaseSubtitle = true     // Tagline fades in
        delay(1800)
        onFinished()
    }

    // ── BACKDROPS ──
    val backdropPaths = listOf(
        "https://image.tmdb.org/t/p/w1280/zDgB8Bsc3xLVKjJMadQVkwO7lTi.jpg",
        "https://image.tmdb.org/t/p/w1280/qjGrUmKW78MCFG8PTVCp6SkOz4K.jpg",
        "https://image.tmdb.org/t/p/w1280/8b8R8l88Qje9dn9OE8PY05Nxl1X.jpg",
        "https://image.tmdb.org/t/p/w1280/yDHYTfA3R0jFYba16jBB1ef8oIt.jpg",
        "https://image.tmdb.org/t/p/w1280/xOMo8BRK7PfcJv9JCnx7s5hj0PX.jpg",
    )
    val selectedBackdrop = remember { backdropPaths.random() }

    // ── INFINITE TRANSITIONS ──
    val infinite = rememberInfiniteTransition(label = "splash_inf")

    // Ken Burns: slow zoom 1.0 → 1.15
    val backdropScale by animateFloatAsState(
        targetValue = if (phaseBackdrop) 1.15f else 1.0f,
        animationSpec = tween(4000, easing = LinearEasing),
        label = "ken_burns",
    )

    // Backdrop fade-in
    val backdropAlpha by animateFloatAsState(
        targetValue = if (phaseBackdrop) 0.35f else 0f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "backdrop_alpha",
    )

    // Scan-line sweep: 0 → 1 across the screen
    val scanProgress by animateFloatAsState(
        targetValue = if (phaseScanLine) 1f else 0f,
        animationSpec = tween(700, easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)),
        label = "scan_line",
    )

    // Logo slam: 3x → 1x with heavy overshoot
    val logoScale by animateFloatAsState(
        targetValue = if (phaseLogoSlam) 1f else 3f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 300f),
        label = "logo_slam",
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (phaseLogoSlam) 1f else 0f,
        animationSpec = tween(300),
        label = "logo_alpha",
    )

    // Glow pulse behind logo
    val glowPulse by infinite.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow_pulse",
    )

    // Glow ring scale
    val glowScale by infinite.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow_scale",
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        // ── LAYER 1: BACKDROP IMAGE ──
        AsyncImage(
            model = selectedBackdrop,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .scale(backdropScale)
                .alpha(backdropAlpha),
        )

        // ── LAYER 2: VIGNETTE GRADIENT ──
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            FlixBlack.copy(alpha = 0.3f),
                            FlixBlack.copy(alpha = 0.75f),
                            FlixBlack,
                        ),
                        center = Offset(0.5f, 0.5f),
                        radius = 1400f,
                    )
                )
        )

        // ── LAYER 3: RED SCAN-LINE ──
        if (scanProgress > 0f && scanProgress < 1f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val lineX = size.width * scanProgress
                val lineWidth = 80.dp.toPx()

                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            FlixRed.copy(alpha = 0.6f),
                            FlixRed.copy(alpha = 0.9f),
                            FlixRed.copy(alpha = 0.6f),
                            Color.Transparent,
                        ),
                        startX = lineX - lineWidth,
                        endX = lineX + lineWidth,
                    )
                )
            }
        }

        // ── LAYER 4: EMBER PARTICLES ──
        if (phaseEmbers) {
            EmberField(modifier = Modifier.fillMaxSize())
        }

        // ── LAYER 5: LOGO + TEXT ──
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Red glow sphere behind logo
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(if (phaseLogoSlam) glowScale else 0f)
                    .alpha(if (phaseLogoSlam) glowPulse else 0f)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    FlixRed.copy(alpha = 0.5f),
                                    FlixRed.copy(alpha = 0.15f),
                                    Color.Transparent,
                                ),
                            ),
                            radius = size.minDimension / 2f,
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                // "VTR" logo text — dramatic slam
                Text(
                    text = stringResource(R.string.splash_logo_short),
                    style = TextStyle(
                        color = FlixWhite,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Black,
                        fontSize = 64.sp,
                        shadow = Shadow(
                            color = FlixRed.copy(alpha = 0.8f),
                            offset = Offset(0f, 0f),
                            blurRadius = 32f,
                        ),
                        letterSpacing = 12.sp,
                    ),
                    modifier = Modifier
                        .scale(logoScale)
                        .alpha(logoAlpha),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Full title — "VTR Æ" — slides up from below
            AnimatedVisibility(
                visible = phaseTitle,
                enter = fadeIn(tween(600)) + slideInVertically(
                    animationSpec = spring(dampingRatio = 0.7f, stiffness = 200f),
                    initialOffsetY = { it / 2 },
                ),
            ) {
                Text(
                    text = stringResource(R.string.splash_title),
                    style = TextStyle(
                        color = FlixWhite,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Black,
                        fontSize = 52.sp,
                        shadow = Shadow(
                            color = FlixRed.copy(alpha = 0.6f),
                            offset = Offset(0f, 4f),
                            blurRadius = 20f,
                        ),
                        letterSpacing = 4.sp,
                    ),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline — "Threads of the Æther." — elegant fade
            AnimatedVisibility(
                visible = phaseSubtitle,
                enter = fadeIn(tween(800)),
            ) {
                Text(
                    text = stringResource(R.string.splash_subtitle),
                    style = TextStyle(
                        color = FlixRed.copy(alpha = 0.9f),
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        letterSpacing = 6.sp,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        }
    }
}

// ── EMBER PARTICLE FIELD ───────────────────────────────────

private data class Ember(
    val x: Float,       // normalized 0..1
    val speed: Float,   // normalized rise speed
    val size: Float,    // dp
    val alpha: Float,   // max alpha
    val delay: Long,    // stagger start
)

@Composable
private fun EmberField(modifier: Modifier = Modifier) {
    val config = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeightPx = with(density) { config.screenHeightDp.dp.toPx() }
    val screenWidthPx = with(density) { config.screenWidthDp.dp.toPx() }

    val embers = remember {
        List(24) {
            Ember(
                x = Random.nextFloat(),
                speed = 0.3f + Random.nextFloat() * 0.7f,
                size = 2f + Random.nextFloat() * 4f,
                alpha = 0.3f + Random.nextFloat() * 0.5f,
                delay = (Random.nextFloat() * 1500).toLong(),
            )
        }
    }

    val infinite = rememberInfiniteTransition(label = "embers")

    // Master time driver: 0 → 1 over 4 seconds, repeating
    val time by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
        ),
        label = "ember_time",
    )

    Canvas(modifier = modifier) {
        embers.forEach { ember ->
            // Y position: rises from bottom to top, wrapping around
            val phase = (time * ember.speed + ember.delay / 4000f) % 1f
            val y = screenHeightPx * (1f - phase)

            // X position: gentle horizontal sway
            val x = screenWidthPx * ember.x +
                sin((phase * 6.2831853).toDouble()).toFloat() * 20f

            // Fade out near edges
            val fadeAlpha = ember.alpha * when {
                phase < 0.1f -> phase / 0.1f
                phase > 0.85f -> (1f - phase) / 0.15f
                else -> 1f
            }

            drawCircle(
                color = FlixRed.copy(alpha = fadeAlpha),
                radius = ember.size,
                center = Offset(x, y),
            )

            // Secondary glow around each ember
            drawCircle(
                color = FlixRed.copy(alpha = fadeAlpha * 0.3f),
                radius = ember.size * 3f,
                center = Offset(x, y),
            )
        }
    }
}
