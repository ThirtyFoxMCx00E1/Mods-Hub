package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AmberGta
import com.example.ui.theme.EmeraldMinecraft

enum class GameCategory(
    val id: String,
    val displayName: String,
    val shortName: String,
    val primaryColor: Color,
    val packageFolder: String,
    val supportedFormats: List<String>
) {
    GTA_SA(
        id = "gta_sa",
        displayName = "GTA SA Mobile",
        shortName = "GTA SA",
        primaryColor = AmberGta,
        packageFolder = "emulated/0/Android_unprotected/data/com.rockstargames.gtasa/mods & files/CLEO (or game root)",
        supportedFormats = listOf(".csa", ".csi", ".dff", ".txd", ".dat", ".zip")
    ),
    MINECRAFT_BEDROCK(
        id = "minecraft_bedrock",
        displayName = "Minecraft Bedrock",
        shortName = "Minecraft PE",
        primaryColor = EmeraldMinecraft,
        packageFolder = "Android/data/com.mojang.minecraftpe",
        supportedFormats = listOf(".mcaddon", ".mcpack", ".mcworld", ".zip")
    );

    companion object {
        fun fromId(id: String?): GameCategory? {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) }
        }
    }
}
