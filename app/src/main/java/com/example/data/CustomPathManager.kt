package com.example.data

import android.content.Context
import com.example.model.GameCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PathPreset(
    val title: String,
    val path: String,
    val description: String,
    val gameCategory: GameCategory? = null
)

class CustomPathManager(context: Context) {

    private val prefs = context.getSharedPreferences("modhub_path_prefs", Context.MODE_PRIVATE)

    companion object {
        const val PRESET_GTA_UNPROTECTED = "emulated/0/Android_unprotected/data/com.rockstargames.gtasa/mods"
        const val PRESET_GTA_CLEO = "emulated/0/Android_unprotected/data/com.rockstargames.gtasa/files/CLEO"
        const val PRESET_GTA_ROOT = "emulated/0/Android/data/com.rockstargames.gtasa/files"
        const val PRESET_MC_RESOURCES = "emulated/0/Android/data/com.mojang.minecraftpe/files/games/com.mojang/resource_packs"
        const val PRESET_MC_BEHAVIOR = "emulated/0/Android/data/com.mojang.minecraftpe/files/games/com.mojang/behavior_packs"
        const val PRESET_DOWNLOADS = "emulated/0/Download/Mods"
    }

    val presets = listOf(
        PathPreset(
            title = "GTA SA Unprotected (v1.1+)",
            path = PRESET_GTA_UNPROTECTED,
            description = "Bypasses Android scoped storage restrictions for GTA SA mods",
            gameCategory = GameCategory.GTA_SA
        ),
        PathPreset(
            title = "GTA SA CLEO Scripts",
            path = PRESET_GTA_CLEO,
            description = "Direct placement in game CLEO scripts folder",
            gameCategory = GameCategory.GTA_SA
        ),
        PathPreset(
            title = "GTA SA Root Directory",
            path = PRESET_GTA_ROOT,
            description = "Direct root of GTA San Andreas game files",
            gameCategory = GameCategory.GTA_SA
        ),
        PathPreset(
            title = "Minecraft Resource Packs",
            path = PRESET_MC_RESOURCES,
            description = "Target folder for Bedrock texture packs & models",
            gameCategory = GameCategory.MINECRAFT_BEDROCK
        ),
        PathPreset(
            title = "Minecraft Behavior Packs",
            path = PRESET_MC_BEHAVIOR,
            description = "Target folder for Bedrock behavior scripts & entities",
            gameCategory = GameCategory.MINECRAFT_BEDROCK
        ),
        PathPreset(
            title = "Standard Downloads Folder",
            path = PRESET_DOWNLOADS,
            description = "Standard accessible Downloads folder on phone storage"
        )
    )

    private val _currentPath = MutableStateFlow(
        prefs.getString("custom_download_path", PRESET_GTA_UNPROTECTED) ?: PRESET_GTA_UNPROTECTED
    )
    val currentPath: StateFlow<String> = _currentPath.asStateFlow()

    private val _isCustomOverride = MutableStateFlow(
        prefs.getBoolean("is_custom_path_override", true)
    )
    val isCustomOverride: StateFlow<Boolean> = _isCustomOverride.asStateFlow()

    fun getEffectivePath(game: GameCategory): String {
        if (_isCustomOverride.value) {
            return _currentPath.value
        }
        return when (game) {
            GameCategory.GTA_SA -> PRESET_GTA_UNPROTECTED
            GameCategory.MINECRAFT_BEDROCK -> PRESET_MC_RESOURCES
            GameCategory.ALL -> _currentPath.value
        }
    }

    fun setPath(newPath: String) {
        val trimmed = newPath.trim()
        _currentPath.value = trimmed
        _isCustomOverride.value = true
        prefs.edit()
            .putString("custom_download_path", trimmed)
            .putBoolean("is_custom_path_override", true)
            .apply()
    }

    fun resetToDefault(game: GameCategory) {
        val defaultPath = when (game) {
            GameCategory.GTA_SA -> PRESET_GTA_UNPROTECTED
            GameCategory.MINECRAFT_BEDROCK -> PRESET_MC_RESOURCES
            GameCategory.ALL -> PRESET_DOWNLOADS
        }
        setPath(defaultPath)
    }
}
