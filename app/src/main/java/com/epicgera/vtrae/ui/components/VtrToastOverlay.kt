// FILE_PATH: app/src/main/java/com/epicgera/vtrae/ui/components/VtrToastOverlay.kt
package com.epicgera.vtrae.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

val ToastInfoColor = Color.Cyan
val ToastErrorColor = Color(0xFFE50914) // FlixRed

/**
 * A host composable that should be placed at the root of the app's theme.
 * It listens to the VtrToastManager and displays notifications overlaid on top of content.
 */
@Composable
fun VtrToastHost(modifier: Modifier = Modifier) {
    val messages by VtrToastManager.messages.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .focusProperties { canFocus = false },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 32.dp, start = 32.dp, end = 32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            messages.forEach { message ->
                VtrToast(
                    message = message,
                    onDismiss = { VtrToastManager.dismiss(message.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun VtrToast(
    message: VtrToastMessage,
    onDismiss: () -> Unit
) {
    // Auto-dismiss after 3.5 seconds
    LaunchedEffect(message.id) {
        delay(3500)
        onDismiss()
    }

    val borderColor = when (message.type) {
        VtrToastType.INFO -> ToastInfoColor.copy(alpha = 0.8f)
        VtrToastType.ERROR -> ToastErrorColor.copy(alpha = 0.8f)
    }

    AnimatedVisibility(
        visible = true, // Wrapped in AnimatedVisibility to allow entrance animation
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xD90D0D0D)) // Deep dark gray with 85% opacity (Glassmorphism base)
                .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .focusProperties { canFocus = false }
        ) {
            Text(
                text = message.text,
                color = Color.White,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

