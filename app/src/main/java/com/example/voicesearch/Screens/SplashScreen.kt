package com.example.voicesearch.Screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.voicesearch.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0.6f) }

    LaunchedEffect(Unit) {
        // play scale + fade animation
        scope.launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 650)
            )
        }
        scope.launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 650)
            )
        }

        // total splash visible time
        delay(1200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // replace with your own drawable or vector
        // e.g. res/drawable/ic_splash_logo.xml or a png in drawable
        val logoId = remember { R.drawable.ic_splash_logo } // ensure this exists
        androidx.compose.foundation.Image(
            painter = painterResource(id = logoId),
            contentDescription = "Splash logo",
            modifier = Modifier
                .size(140.dp)
                .scale(scale.value)
                .alpha(alpha.value)
        )
    }
}

