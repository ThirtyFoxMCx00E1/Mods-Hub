package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameCategory
import com.example.ui.theme.AmberGta
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldMinecraft
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun InstallGuideScreen(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val games = listOf("GTA SA Mobile", "Minecraft Bedrock")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Guide header
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateCardBorder))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CyanAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = "Android Mod Installer Manual",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Step-by-step setup for GTA SA data files & Minecraft .mcaddon packs",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SlateCard,
            contentColor = if (selectedTab == 0) AmberGta else EmeraldMinecraft
        ) {
            games.forEachIndexed { index, name ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = name,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (selectedTab == index) {
                                if (index == 0) AmberGta else EmeraldMinecraft
                            } else TextSecondary
                        )
                    }
                )
            }
        }

        if (selectedTab == 0) {
            // GTA SA Mobile Guide
            GuideSection(
                title = "1. GTA SA Data Directory Structure",
                steps = listOf(
                    "All GTA SA mods reside in: emulated/0/Android_unprotected/data/com.rockstargames.gtasa/mods & files/CLEO or direct in the root of the game.",
                    "On Android 11, 12, 13, 14, and 15, using Android_unprotected allows direct file copying without Scoped Storage restrictions.",
                    "For CLEO scripts: Copy .csa and .csi files into files/CLEO or directly in the root of the game.",
                    "For Cars and Textures: Place the texdb/ folder and data/handling.cfg into mods/ or directly in game root."
                ),
                color = AmberGta
            )

            GuideSection(
                title = "2. How to Activate CLEO Touch Menu",
                steps = listOf(
                    "Install any CLEO Gold / Menu script downloaded from ModHub.",
                    "Launch GTA SA Mobile and tap 'Resume' or 'Start Game'.",
                    "Swipe your finger firmly from the very top of the screen downwards to open the cheat overlay.",
                    "Tap on-screen directional buttons to browse cheats, vehicle spawn lists, and weapon wheels."
                ),
                color = AmberGta
            )
        } else {
            // Minecraft Bedrock Guide
            GuideSection(
                title = "1. Auto-Importing .mcaddon and .mcpack",
                steps = listOf(
                    "Download the .mcaddon or .mcpack mod file from our verified Blogspot catalog.",
                    "Open the ModHub Downloads tab and tap 'Open in Minecraft'.",
                    "Minecraft Bedrock will boot immediately and display 'Import Started' in the top notification banner.",
                    "Wait until Minecraft confirms 'Successfully imported [Mod Name]'."
                ),
                color = EmeraldMinecraft
            )

            GuideSection(
                title = "2. Activating Addons in Your World",
                steps = listOf(
                    "Open Minecraft and click 'Edit' (pencil icon) next to your chosen world.",
                    "Scroll down in world settings to 'Resource Packs' and click 'My Packs' > 'Activate'.",
                    "Switch to 'Behavior Packs' and activate the corresponding behavior pack.",
                    "Under 'Experiments' toggle ON: 'Holiday Creator Features', 'Custom Biomes', and 'Upcoming Creator Features'.",
                    "Click Play and enjoy your custom world!"
                ),
                color = EmeraldMinecraft
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun GuideSection(
    title: String,
    steps: List<String>,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(SlateCardBorder)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            steps.forEachIndexed { i, text ->
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${i + 1}",
                            color = color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
