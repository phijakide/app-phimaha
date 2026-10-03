package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.SupplyFlowViewModel
import com.example.ui.theme.SecondaryLight
import com.example.ui.theme.StatusSuccess

@Composable
fun AudioTranscriptionScreen(
    viewModel: SupplyFlowViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()
    val savedMemos by viewModel.voiceMemos.collectAsStateWithLifecycle()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            viewModel.startMicrophoneRecording()
        } else {
            Toast.makeText(context, "Microphone permission is required to record audio.", Toast.LENGTH_SHORT).show()
        }
    }

    // Pulse animation when recording
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("audio_transcription_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SecondaryLight
                        ) {
                            Text(
                                text = "MODEL: gemini-3.5-transcribe",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        if (audioState.isRecording) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "LIVE RECORDING",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Voice Memo & Audio Transcription",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Dictate field audit memos, count voice logs, and damage dispatches via microphone.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Live Microphone Recording Studio Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (audioState.isRecording) "Listening to Microphone..." else "Tap Microphone to Speak",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (audioState.isRecording) {
                            String.format("Recording: %02d:%02d", audioState.recordingDurationSec / 60, audioState.recordingDurationSec % 60)
                        } else {
                            "Ready to transcribe with gemini-3.5-transcribe"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (audioState.isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Pulse button
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(if (audioState.isRecording) pulseScale else 1f)
                            .clip(CircleShape)
                            .background(
                                if (audioState.isRecording) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.primary
                            )
                            .border(
                                width = 4.dp,
                                color = if (audioState.isRecording) MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                if (audioState.isRecording) {
                                    viewModel.stopAndTranscribeMicrophoneRecording()
                                } else {
                                    if (hasPermission) {
                                        viewModel.startMicrophoneRecording()
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("mic_record_button")
                        ) {
                            Icon(
                                imageVector = if (audioState.isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = if (audioState.isRecording) "Stop Recording" else "Record Audio",
                                tint = Color.White,
                                modifier = Modifier.size(42.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (audioState.isRecording) {
                                viewModel.stopAndTranscribeMicrophoneRecording()
                            } else {
                                if (hasPermission) {
                                    viewModel.startMicrophoneRecording()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (audioState.isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth(0.85f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (audioState.isRecording) "Stop & Transcribe Now" else "Start Microphone Recording",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (audioState.isTranscribing) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Transcribing with gemini-3.5-transcribe...",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }

        // Quick Preset Audio Memos for Instant Verification
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Or Test with Sample Logistics Audio Memos",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.transcribeSampleAudio("Warehouse stock count for Aisle 04: 42 controllers verified in bin B-12. Zero discrepancies.")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sample_memo_1"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Aisle 4 Count", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                viewModel.transcribeSampleAudio("Inbound carrier delivery dock 2: 15 cartons received, 1 carton has exterior tear on edge.")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sample_memo_2"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Dock 2 Inbound", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Live Transcribed Output Card
        if (audioState.transcribedText != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transcription_result_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Transcription Output",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Transcription", audioState.transcribedText)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Transcribed text copied!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy text")
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(
                            text = "\"${audioState.transcribedText}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }

        // Live API Voice Conversations (gemini-3.8-live)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("live_api_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.tertiary
                        ) {
                            Text(
                                text = "MODEL: gemini-3.8-live (Live API)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Icon(Icons.Default.Sensors, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Real-Time Hands-Free Voice Conversation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Converse with gemini-3.8-live directly for instant hands-free field updates and real-time audio interaction.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                viewModel.runLiveVoiceConversation("Check inbound delivery dock 3 status and verify forklift battery reserves.")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        ) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Dock 3 Live Voice Check")
                        }
                    }

                    if (audioState.isLiveConversationActive) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Live API streaming conversational audio...", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    if (audioState.liveConversationLog.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                audioState.liveConversationLog.takeLast(4).forEach { entry ->
                                    Text(
                                        text = entry,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Saved Voice Memos from Room DB
        item {
            Text(
                text = "Saved Field Voice Memos (Local Database)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (savedMemos.isEmpty()) {
            item {
                Text(
                    text = "No saved voice memos yet. Record your first memo above.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(savedMemos) { memo ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = memo.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { viewModel.deleteVoiceMemo(memo.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                        Text(
                            text = "Model: ${memo.modelUsed} • Category: ${memo.category}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = memo.transcribedText,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
