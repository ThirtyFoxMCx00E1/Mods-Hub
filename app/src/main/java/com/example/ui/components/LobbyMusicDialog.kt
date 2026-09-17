package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.LobbyMusicManager
import com.example.audio.SoundEffectManager

@Composable
fun LobbyMusicDialog(
    lobbyMusicManager: LobbyMusicManager,
    soundEffectManager: SoundEffectManager,
    onDismiss: () -> Unit
) {
    val isPlaying by lobbyMusicManager.isPlaying.collectAsState()
    val isLooping by lobbyMusicManager.isLooping.collectAsState()
    val volume by lobbyMusicManager.volume.collectAsState()
    val trackTitle by lobbyMusicManager.trackTitle.collectAsState()
    val isCustomTrack by lobbyMusicManager.isCustomTrack.collectAsState()
    val currentPositionMs by lobbyMusicManager.currentPositionMs.collectAsState()
    val durationMs by lobbyMusicManager.durationMs.collectAsState()
    val isSoundEnabled by soundEffectManager.isSoundEnabled.collectAsState()

    // File picker for custom audio (e.g. videoplayback (2).m4a)
    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "videoplayback (2).m4a"
            lobbyMusicManager.setCustomAudioUri(uri, fileName)
            soundEffectManager.playClick()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("lobby_music_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = "Music",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Lobby Music & Audio",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "2h Looping Track & UI Click Effects",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            soundEffectManager.playClick()
                            onDismiss()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                // Currently Playing Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0A0F1D),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isPlaying) Color(0xFF10B981) else Color.Gray,
                                modifier = Modifier.size(8.dp)
                            ) {}
                            Text(
                                text = if (isPlaying) "NOW PLAYING (LOOPED)" else "PAUSED",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isPlaying) Color(0xFF10B981) else Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = trackTitle,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        // Timer Display
                        Text(
                            text = "${formatTime(currentPositionMs)} / ${if (durationMs > 0) formatTime(durationMs) else "02:24:55"}",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF00E5FF),
                            fontSize = 12.sp
                        )

                        // Progress bar
                        LinearProgressIndicator(
                            progress = {
                                if (durationMs > 0) {
                                    (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
                                } else 0.05f
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF10B981),
                            trackColor = Color(0xFF1E293B)
                        )

                        // Primary Playback Controls
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Looping Toggle
                            IconButton(
                                onClick = {
                                    soundEffectManager.playClick()
                                    lobbyMusicManager.setLooping(!isLooping)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = "Looping",
                                    tint = if (isLooping) Color(0xFF00E5FF) else Color(0xFF64748B)
                                )
                            }

                            // Play / Pause Circle
                            FilledIconButton(
                                onClick = {
                                    soundEffectManager.playClick()
                                    lobbyMusicManager.togglePlayPause()
                                },
                                modifier = Modifier
                                    .size(54.dp)
                                    .testTag("lobby_play_pause_button"),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = Color(0xFF10B981),
                                    contentColor = Color(0xFF0A0E17)
                                )
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            // Volume Mute/Unmute
                            IconButton(
                                onClick = {
                                    soundEffectManager.playClick()
                                    if (volume > 0.05f) {
                                        lobbyMusicManager.setVolume(0f)
                                    } else {
                                        lobbyMusicManager.setVolume(0.75f)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (volume > 0.05f) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                    contentDescription = "Volume",
                                    tint = if (volume > 0.05f) Color.White else Color(0xFFEF4444)
                                )
                            }
                        }

                        // Volume Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeDown,
                                contentDescription = "Min Volume",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                            Slider(
                                value = volume,
                                onValueChange = { lobbyMusicManager.setVolume(it) },
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF10B981),
                                    activeTrackColor = Color(0xFF10B981),
                                    inactiveTrackColor = Color(0xFF1E293B)
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Max Volume",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // File Loader Option for "videoplayback (2).m4a"
                OutlinedButton(
                    onClick = {
                        soundEffectManager.playClick()
                        audioPicker.launch("audio/*")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("select_custom_music_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5FF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.AudioFile,
                        contentDescription = "Load Music",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCustomTrack) "Change Custom Audio / videoplayback.m4a" else "Load Custom 2h Track (videoplayback)",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                if (isCustomTrack) {
                    TextButton(
                        onClick = {
                            soundEffectManager.playClick()
                            lobbyMusicManager.resetToDefaultLobbyMusic()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reset to Default Looping Lobby Track", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                }

                HorizontalDivider(color = Color(0xFF1E293B))

                // Button Click Sound Effect (Mystery Alert) Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0A0F1D))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFB300).copy(alpha = 0.15f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Click Chime",
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Button Click Sound Effect",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Mystery Alert chime on any UI click",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = isSoundEnabled,
                        onCheckedChange = {
                            soundEffectManager.toggleSoundEnabled()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFFB300),
                            checkedTrackColor = Color(0xFFFFB300).copy(alpha = 0.4f),
                            uncheckedThumbColor = Color(0xFF64748B),
                            uncheckedTrackColor = Color(0xFF1E293B)
                        )
                    )
                }

                // Done Button
                Button(
                    onClick = {
                        soundEffectManager.playClick()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E293B),
                        contentColor = Color.White
                    )
                ) {
                    Text("Close Audio Settings")
                }
            }
        }
    }
}

private fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
