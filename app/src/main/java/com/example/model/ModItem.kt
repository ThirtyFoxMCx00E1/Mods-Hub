package com.example.model

data class ModItem(
    val id: String,
    val title: String,
    val game: GameCategory,
    val category: String, // e.g. CLEO Script, Texture Pack, Shader, Vehicle Pack, Addon
    val version: String,
    val fileSize: String,
    val author: String,
    val summary: String,
    val blogSpotUrl: String,
    val directDownloadUrl: String,
    val fileExtension: String, // e.g. .zip, .mcaddon, .mcpack
    val targetVersion: String, // e.g. "GTA SA v2.00/2.10", "Minecraft PE 1.20 - 1.21"
    val bannerDrawableRes: Int? = null,
    val rating: Float = 4.8f,
    val downloadCount: Int = 1250,
    val publishDate: String,
    val htmlContent: String,
    val installSteps: List<String>,
    val features: List<String>,
    val isFeatured: Boolean = false
)
