package com.example.voicesearch.Screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.voicesearch.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var showLoading by remember { mutableStateOf(false) }

    val scale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(-10f) }
    val glowScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        coroutineScope {
            launch {
                scale.animateTo(
                    targetValue = 1.15f,
                    animationSpec = tween(450, easing = FastOutSlowInEasing)
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(200, easing = FastOutSlowInEasing)
                )
            }

            launch {
                logoAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(500)
                )
            }

            launch {
                rotation.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(550, easing = FastOutSlowInEasing)
                )
            }

            launch {
                glowScale.animateTo(
                    targetValue = 1.2f,
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                )
                glowScale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(200)
                )
            }
        }

        // Show loading for a moment
        delay(900)
        showLoading = true

        delay(1200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0092FF),      // Bright blue top like your logo
                        Color(0xFF0074FF),      // Deep blue middle
                        Color(0xFF0059E8)       // Bottom darker gradient
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        // Glow behind logo
        Box(
            modifier = Modifier
                .size(220.dp * glowScale.value)
                .graphicsLayer { alpha = 0.3f * logoAlpha.value }
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.4f), Color.Transparent)
                    )
                )
        )

        // Logo
        Image(
            painter = painterResource(id = R.drawable.ic_splash_logo),
            contentDescription = "Splash Logo",
            modifier = Modifier
                .size(160.dp)
                .scale(scale.value)
                .alpha(logoAlpha.value)
                .graphicsLayer {
                    rotationZ = rotation.value
                    shadowElevation = 20.dp.toPx()
                }
        )

        // LOADING BELOW LOGO
        if (showLoading) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(38.dp)
                )
            }
        }
    }
}
