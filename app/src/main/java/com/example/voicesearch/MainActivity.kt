package com.example.voicesearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.voicesearch.Screens.MainScaffold
import com.example.voicesearch.Screens.SplashScreen
import com.example.voicesearch.ui.theme.VoiceSearchTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VoiceSearchTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(
                        onFinished = {
                            showSplash = false
                        }
                    )
                } else {
                    MainScaffold()
                }
            }
        }
    }
}
