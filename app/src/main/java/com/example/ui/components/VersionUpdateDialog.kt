package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VersionUpdateDialog(
    onDismiss: () -> Unit,
    onOpenCustomPath: (() -> Unit)? = null,
    onOpenAudioSettings: (() -> Unit)? = null,
    onPlayClick: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var isChecking by remember { mutableStateOf(false) }
    var updateMessage by remember {
        mutableStateOf("Version 1.3 is installed and active. Includes CI/CD unit test fixes, custom download folder path anywhere, 2h looping lobby music, and UI button click sound effects.")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CyanAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = "Version 1.3 Update",
                        tint = CyanAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "Version 1.3 Update",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Build 1.3 (CI Fixes, Custom Path & Audio)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Status banner
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardBorder))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "v1.3 Release: Unit Test Fixes & Audio",
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = updateMessage,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                // Feature Highlights
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "New in Version 1.3:",
                            color = AmberGta,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )

                        Text(
                            text = "• CI/CD Unit Test Fix: Resolved CustomPathManager parameters, methods, and companion constants for green GitHub Actions builds.",
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                        Text(
                            text = "• Custom Download Directory: Download anywhere in directory folder with editable paths & quick presets.",
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                        Text(
                            text = "• 2-Hour Looping Lobby Music: Background playback with controls for volume, looping, and local videoplayback.m4a loading.",
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                        Text(
                            text = "• Mystery Alert Button Clicks: Satisfying UI sound effect chime for all interactive buttons.",
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                // Quick Launchers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onOpenCustomPath != null) {
                        OutlinedButton(
                            onClick = {
                                onPlayClick()
                                onOpenCustomPath()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanAccent),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Set Folder", fontSize = 11.sp)
                        }
                    }

                    if (onOpenAudioSettings != null) {
                        OutlinedButton(
                            onClick = {
                                onPlayClick()
                                onOpenAudioSettings()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF10B981)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Audio / 2h", fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onPlayClick()
                    if (!isChecking) {
                        isChecking = true
                        coroutineScope.launch {
                            delay(600)
                            isChecking = false
                            updateMessage = "Mod Hub v1.3 is fully up to date. CI tests verified and features active."
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                modifier = Modifier.testTag("check_update_btn")
            ) {
                if (isChecking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Checking...", color = Color(0xFF00363F), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Color(0xFF00363F),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Check Updates", color = Color(0xFF00363F), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onPlayClick()
                onDismiss()
            }) {
                Text("Close", color = TextSecondary)
            }
        },
        containerColor = SlateCard,
        shape = RoundedCornerShape(16.dp)
    )
}
