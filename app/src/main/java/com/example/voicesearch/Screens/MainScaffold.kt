package com.example.voicesearch.Screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.voicesearch.R

@Composable
fun MainScaffold() {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    var transcript by remember { mutableStateOf("") }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (!granted) {
                Toast.makeText(
                    context,
                    "Microphone permission is required for voice input",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    // Request permission when this composable enters composition (if not already granted)
    LaunchedEffect(Unit) {
        if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // SpeechRecognizer instance lifecycle
    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else null
    }

    DisposableEffect(speechRecognizer) {
        onDispose {
            speechRecognizer?.destroy()
        }
    }

    // Set up recognition listener
    LaunchedEffect(speechRecognizer) {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) { /* no-op */ }
            override fun onBeginningOfSpeech() { /* no-op */ }
            override fun onRmsChanged(p0: Float) { /* no-op */ }
            override fun onBufferReceived(p0: ByteArray?) { /* no-op */ }
            override fun onEndOfSpeech() { /* no-op */ }
            override fun onError(error: Int) {
                isListening = false
                val message = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech matched"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    else -> "Recognition error: $error"
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    // append or replace transcript based on your preference
                    transcript = matches.joinToString(separator = " ")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!partial.isNullOrEmpty()) {
                    // show live partial transcript
                    transcript = partial.joinToString(separator = " ")
                }
            }

            override fun onEvent(p0: Int, p1: Bundle?) { /* no-op */ }
        })
    }

    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "VoiceSearch",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(18.dp))

                Text(
                    text = transcript.ifBlank { "Tap mic and speak" },
                    modifier = Modifier
                        .padding(12.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(18.dp))

                // Mic button
                IconButton(
                    onClick = {
                        // ask for permission if not granted
                        if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            return@IconButton
                        }

                        if (speechRecognizer == null) {
                            Toast.makeText(context, "Speech recognition not available on this device", Toast.LENGTH_SHORT).show()
                            return@IconButton
                        }

                        if (!isListening) {
                            // start listening
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).also {
                                it.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                it.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                                it.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                            }
                            speechRecognizer.startListening(intent)
                            isListening = true
                        } else {
                            // stop listening
                            speechRecognizer.stopListening()
                            isListening = false
                        }
                    },
                    modifier = Modifier
                        .size(90.dp)
                        .background(
                            color = if (isListening) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.mic), // provide a mic icon in drawable
                        contentDescription = "Mic",
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                }

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))

                // Search button
                Button(onClick = {
                    val query = transcript.trim()
                    if (query.isBlank()) {
                        Toast.makeText(context, "Say something first", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    // Open a Google search in browser
                    val url = "https://www.google.com/search?q=${Uri.encode(query)}"
                    val searchIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(searchIntent)
                }) {
                    Text(text = "Search")
                }
            }
        }
    }
}

