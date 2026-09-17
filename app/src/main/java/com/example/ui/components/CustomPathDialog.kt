package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.CustomPathManager
import com.example.data.PathPreset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomPathDialog(
    customPathManager: CustomPathManager,
    onDismiss: () -> Unit,
    onPlayClick: () -> Unit = {}
) {
    val currentPath by customPathManager.currentPath.collectAsState()
    var inputPath by remember { mutableStateOf(currentPath) }
    var selectedPresetIndex by remember { mutableStateOf(-1) }
    val clipboardManager = LocalClipboardManager.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("custom_path_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161E2E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF00E5FF).copy(alpha = 0.15f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = "Folder",
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Custom Download Path",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Target any directory folder on device (v1.2)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF90A4AE)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            onPlayClick()
                            onDismiss()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF90A4AE)
                        )
                    }
                }

                // Active Path Preview
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0A0F1D),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ACTIVE TARGET DIRECTORY",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF00E5FF),
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = {
                                    onPlayClick()
                                    clipboardManager.setText(AnnotatedString(inputPath))
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Path",
                                    tint = Color(0xFF90A4AE),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = inputPath,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp
                        )
                    }
                }

                // Custom editable text field
                OutlinedTextField(
                    value = inputPath,
                    onValueChange = {
                        inputPath = it
                        selectedPresetIndex = -1
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_path_input"),
                    label = { Text("Custom Path (Anywhere in storage)", color = Color(0xFF90A4AE)) },
                    placeholder = { Text("e.g. emulated/0/MyFolder/Mods", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color(0xFFE2E8F0),
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF334155),
                        cursorColor = Color(0xFF00E5FF)
                    ),
                    singleLine = false,
                    maxLines = 3,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF00E5FF)
                        )
                    }
                )

                // Quick Presets Title
                Text(
                    text = "Quick Directory Presets",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFFFB300),
                    fontWeight = FontWeight.Bold
                )

                // Presets List
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    customPathManager.presets.forEachIndexed { index, preset ->
                        val isSelected = selectedPresetIndex == index || inputPath == preset.path
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    onPlayClick()
                                    selectedPresetIndex = index
                                    inputPath = preset.path
                                },
                            color = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.12f) else Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF00E5FF) else Color(0xFF1E293B)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        onPlayClick()
                                        selectedPresetIndex = index
                                        inputPath = preset.path
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Color(0xFF00E5FF),
                                        unselectedColor = Color(0xFF64748B)
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = preset.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isSelected) Color(0xFF00E5FF) else Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = preset.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = preset.path,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF64748B),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onPlayClick()
                            inputPath = CustomPathManager.PRESET_GTA_UNPROTECTED
                            selectedPresetIndex = 0
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Text("Reset Default")
                    }

                    Button(
                        onClick = {
                            onPlayClick()
                            if (inputPath.isNotBlank()) {
                                customPathManager.setPath(inputPath)
                            }
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("apply_path_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00E5FF),
                            contentColor = Color(0xFF0A0E17)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Set Destination", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
