package com.example.data

import com.example.R
import com.example.model.GameCategory
import com.example.model.ModItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BlogspotModRepository {

    private val _currentBlogspotSource = MutableStateFlow("https://gtamc-modhub.blogspot.com")
    val currentBlogspotSource: StateFlow<String> = _currentBlogspotSource.asStateFlow()

    private val _bookmarkedIds = MutableStateFlow<Set<String>>(emptySet())
    val bookmarkedIds: StateFlow<Set<String>> = _bookmarkedIds.asStateFlow()

    fun updateBlogspotSource(url: String) {
        if (url.isNotBlank()) {
            _currentBlogspotSource.value = url.trim()
        }
    }

    fun toggleBookmark(modId: String) {
        val current = _bookmarkedIds.value
        _bookmarkedIds.value = if (current.contains(modId)) {
            current - modId
        } else {
            current + modId
        }
    }

    fun isBookmarked(modId: String): Boolean {
        return _bookmarkedIds.value.contains(modId)
    }

    fun getMods(): List<ModItem> = curatedBlogspotMods

    fun getModById(id: String): ModItem? {
        return curatedBlogspotMods.firstOrNull { it.id == id }
    }

    companion object {
        val curatedBlogspotMods: List<ModItem> = listOf(
            ModItem(
                id = "gta_cleo_menu",
                title = "CLEO Gold Cheat & Weapon Menu v2.10",
                game = GameCategory.GTA_SA,
                category = "CLEO Script",
                version = "v2.10.4",
                fileSize = "4.2 MB",
                author = "ModHub Official Editor",
                summary = "Full touch-optimized CLEO menu with car spawner, teleport, infinite health, weapons, and jetpack for GTA San Andreas Mobile.",
                blogSpotUrl = "https://gtamc-modhub.blogspot.com/2026/09/gta-sa-cleo-gold-menu-android.html",
                directDownloadUrl = "https://github.com/aistudio-sample/files/raw/main/mods/gta_cleo_menu_v2.10.zip",
                fileExtension = ".zip",
                targetVersion = "GTA SA Android 2.00 / 2.10 (ARM64 & ARMv7)",
                bannerDrawableRes = R.drawable.banner_gta_sa,
                rating = 4.9f,
                downloadCount = 48200,
                publishDate = "Sep 14, 2026",
                isFeatured = true,
                features = listOf(
                    "Swipe down from top of screen to activate touch UI",
                    "Over 80+ cheats (Wanted level, money, immortality, time speed)",
                    "Complete vehicle spawner with customized sports cars",
                    "Teleport to any location on the San Andreas map",
                    "No root required for Android 10, 11, 12, 13, 14, 15"
                ),
                installSteps = listOf(
                    "Extract the downloaded .zip file using any zip file manager",
                    "Copy all .csa and .csi script files",
                    "Navigate to: emulated/0/Android_unprotected/data/com.rockstargames.gtasa/mods & files/CLEO or direct in the root of the game",
                    "Paste the CLEO script files into files/CLEO or directly in the game root",
                    "Launch GTA SA, start game, and swipe down from screen center to open"
                ),
                htmlContent = """
                    <div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; color: #e2e8f0; line-height: 1.6;">
                        <div style="background: linear-gradient(135deg, #1e293b, #0f172a); border-radius: 12px; padding: 16px; margin-bottom: 20px; border: 1px solid #334155;">
                            <span style="background: #f59e0b; color: #000; font-weight: bold; padding: 4px 10px; border-radius: 6px; font-size: 12px; text-transform: uppercase;">Official Blogspot Post</span>
                            <h1 style="color: #f8fafc; margin: 12px 0 6px 0; font-size: 22px;">GTA SA Mobile CLEO Gold Menu (Android APK & OBB Compatible)</h1>
                            <p style="color: #94a3b8; font-size: 13px; margin: 0;">Author: <strong>Blogspot Master Curator</strong> | Date: September 14, 2026 | Category: CLEO Scripts</p>
                        </div>
                        
                        <h2 style="color: #38bdf8; font-size: 18px; border-bottom: 2px solid #38bdf8; padding-bottom: 6px;">Mod Description & Highlights</h2>
                        <p>Welcome to our official mobile mod repository! Today we are showcasing the definitive <strong>CLEO Gold Menu for GTA San Andreas Mobile</strong>. Fully tested on Android 11 through Android 15 with Scoped Storage support.</p>
                        
                        <div style="background: #1e293b; padding: 14px; border-radius: 8px; border-left: 4px solid #f59e0b; margin: 16px 0;">
                            <h3 style="color: #fbbf24; margin: 0 0 8px 0; font-size: 15px;">Key Capabilities:</h3>
                            <ul style="margin: 0; padding-left: 20px; color: #cbd5e1;">
                                <li>Instant Touch Gesture Activation (Swipe from top to bottom)</li>
                                <li>All Missions Unlocker & Savegame Selector</li>
                                <li>Full Vehicle Spawner with paintjob & tuning presets</li>
                                <li>Super CJ Physics: High jump, fly mode, and fast sprint</li>
                                <li>Custom Weapon Wheel with infinite ammo and explosive bullets</li>
                            </ul>
                        </div>

                        <h2 style="color: #38bdf8; font-size: 18px; border-bottom: 2px solid #38bdf8; padding-bottom: 6px;">Step-by-Step Installation Guide</h2>
                        <ol style="padding-left: 20px; color: #cbd5e1;">
                            <li>Download the mod package below (Size: <strong>4.2 MB</strong>).</li>
                            <li>Extract the archive into your device internal storage.</li>
                            <li>Move files to target path:<br><code style="background: #0f172a; padding: 6px 10px; border-radius: 6px; color: #38bdf8; display: inline-block; margin-top: 6px; word-break: break-all;">emulated/0/Android_unprotected/data/com.rockstargames.gtasa/mods & files/CLEO or direct in the root of the game</code></li>
                            <li>Ensure your GTA SA APK has CLEO library active or use the standard patched APK.</li>
                            <li>Start game and swipe two fingers downwards on screen.</li>
                        </ol>

                        <div style="background: #131b2a; border: 1px dashed #3b82f6; border-radius: 8px; padding: 14px; margin-top: 24px; text-align: center;">
                            <span style="color: #93c5fd; font-weight: 600; font-size: 14px;">Publisher Verification Status</span>
                            <p style="color: #94a3b8; font-size: 12px; margin: 6px 0 0 0;">Checksum SHA256: <code>a87f2e4b89c091...verified safe</code></p>
                            <p style="color: #64748b; font-size: 11px; margin-top: 4px;">Published by Blogspot Administrator (Version 1.0 Repository)</p>
                        </div>
                    </div>
                """.trimIndent()
            ),
            ModItem(
                id = "mc_better_shaders",
                title = "Lush RTX Vibrant Shaders (Render Dragon 1.21)",
                game = GameCategory.MINECRAFT_BEDROCK,
                category = "Shaders & Textures",
                version = "v3.4",
                fileSize = "18.6 MB",
                author = "Bedrock Studio Publisher",
                summary = "Breathtaking realistic lighting, realistic waving water with reflections, dynamic sunlight rays, and vibrant foliage for Minecraft PE.",
                blogSpotUrl = "https://gtamc-modhub.blogspot.com/2026/09/minecraft-bedrock-lush-rtx-shaders.html",
                directDownloadUrl = "https://github.com/aistudio-sample/files/raw/main/mods/lush_rtx_shaders_v3.4.mcpack",
                fileExtension = ".mcpack",
                targetVersion = "Minecraft Bedrock 1.20 - 1.21.x",
                bannerDrawableRes = R.drawable.banner_minecraft,
                rating = 4.95f,
                downloadCount = 65400,
                publishDate = "Sep 15, 2026",
                isFeatured = true,
                features = listOf(
                    "Optimized for Render Dragon engine without lag",
                    "Dynamic sun rays and realistic golden hour sunset glow",
                    "Crystal clear waving water shader with realistic caustics",
                    "Foliage and leaves wind wave animation",
                    "Clean dark starry night skies with aurora borealis"
                ),
                installSteps = listOf(
                    "Download the .mcpack file directly",
                    "Tap on the completed download notification or file in our Downloads tab",
                    "Select 'Minecraft' from the open-with app chooser",
                    "Minecraft will launch automatically and import the resource pack",
                    "Go to Settings > Global Resources > Active > Activate 'Lush RTX Shader'"
                ),
                htmlContent = """
                    <div style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; color: #e2e8f0; line-height: 1.6;">
                        <div style="background: linear-gradient(135deg, #064e3b, #022c22); border-radius: 12px; padding: 16px; margin-bottom: 20px; border: 1px solid #059669;">
                            <span style="background: #10b981; color: #022c22; font-weight: bold; padding: 4px 10px; border-radius: 6px; font-size: 12px; text-transform: uppercase;">Verified Blogspot Release</span>
                            <h1 style="color: #f8fafc; margin: 12px 0 6px 0; font-size: 22px;">Lush RTX Realistic Shaders for Minecraft Bedrock (MCPE 1.21+)</h1>
                            <p style="color: #6ee7b7; font-size: 13px; margin: 0;">Author: <strong>Minecraft Blog Curator</strong> | Date: September 15, 2026 | Category: Shaders</p>
                        </div>

                        <h2 style="color: #34d399; font-size: 18px; border-bottom: 2px solid #34d399; padding-bottom: 6px;">Visual Enhancements</h2>
                        <p>Specially engineered for Minecraft Bedrock's newest Render Dragon graphics pipeline! This shader brings PC-grade atmosphere directly onto Android phones and tablets with ultra high framerate optimization.</p>
                        
                        <div style="background: #0f291e; padding: 14px; border-radius: 8px; border-left: 4px solid #10b981; margin: 16px 0;">
                            <h3 style="color: #6ee7b7; margin: 0 0 8px 0; font-size: 15px;">Included Visual Features:</h3>
                            <ul style="margin: 0; padding-left: 20px; color: #cbd5e1;">
                                <li>Realistic dynamic tone-mapping and volumetric clouds</li>
                                <li>Custom liquid physics: transparent lakes and rivers with sun reflection</li>
                                <li>Ore glow: Diamond, redstone, and emerald blocks emit subtle ambient light</li>
                                <li>Smooth 60 FPS performance tested on mid-range Android devices</li>
                            </ul>
                        </div>

                        <h2 style="color: #34d399; font-size: 18px; border-bottom: 2px solid #34d399; padding-bottom: 6px;">One-Tap Android Installation (.mcpack)</h2>
                        <p>Installing on Bedrock is effortless:</p>
                        <ol style="padding-left: 20px; color: #cbd5e1;">
                            <li>Click the <strong>Download Mod (.mcpack)</strong> button below.</li>
                            <li>Once downloaded, tap the file in our app or in your notification bar.</li>
                            <li>Choose <strong>Open with Minecraft</strong>.</li>
                            <li>The game will display <em>'Import started...'</em> followed by <em>'Successfully imported Lush RTX'</em>.</li>
                            <li>Activate in <em>Global Resources</em> and enter your world!</li>
                        </ol>

                        <div style="background: #111e18; border: 1px solid #059669; border-radius: 8px; padding: 12px; margin-top: 20px;">
                            <p style="color: #a7f3d0; font-size: 12px; margin: 0;"><strong>Ownership Policy:</strong> Mod tested and packed by the Blogspot administrator for stability. User uploads are restricted in v1.0 to protect world saves.</p>
                        </div>
                    </div>
                """.trimIndent()
            ),
            ModItem(
                id = "gta_hd_cars",
                title = "Ultra HD Real Vehicle Pack (50+ Supercars)",
                game = GameCategory.GTA_SA,
                category = "Vehicle Pack",
                version = "v1.8",
                fileSize = "84.5 MB",
                author = "ModHub Official Editor",
                summary = "Replaces in-game low-poly cars with high-detail real sports cars: Lamborghini, Ferrari, Nissan GT-R, BMW M4, and Porsche with realistic handling.",
                blogSpotUrl = "https://gtamc-modhub.blogspot.com/2026/09/gta-sa-real-cars-pack-android.html",
                directDownloadUrl = "https://github.com/aistudio-sample/files/raw/main/mods/gta_hd_cars_v1.8.zip",
                fileExtension = ".zip",
                targetVersion = "GTA SA Android v2.00+",
                bannerDrawableRes = R.drawable.banner_gta_sa,
                rating = 4.88f,
                downloadCount = 31900,
                publishDate = "Sep 10, 2026",
                features = listOf(
                    "50+ meticulously converted real vehicles with working lights",
                    "Custom handling.dat for drifting, top speed, and braking",
                    "High-resolution interior dashboards and steering wheels",
                    "Zero crash guarantee for Android GPU Mali, Adreno, and PowerVR",
                    "Includes pre-configured gta3.img and txd files"
                ),
                installSteps = listOf(
                    "Download and unzip the vehicle pack",
                    "Copy the 'texdb' and 'data' folders",
                    "Paste into emulated/0/Android_unprotected/data/com.rockstargames.gtasa/mods or files/CLEO (or direct in the root of the game)",
                    "Merge folders when prompted by your file manager",
                    "Launch GTA SA and spawn vehicles using the CLEO spawner"
                ),
                htmlContent = """
                    <div style="font-family: sans-serif; color: #e2e8f0; line-height: 1.6;">
                        <h2 style="color: #f59e0b;">GTA San Andreas Mobile: 50+ Real Car Pack</h2>
                        <p>Turn Los Santos into a luxury supercar playground! This vehicle pack upgrades standard in-game cars into licensed-style exotic supercars, classic muscle, and tuned tuner cars.</p>
                        <h3>Featured Vehicles:</h3>
                        <ul>
                            <li>Infernus -> Lamborghini Aventador SVJ</li>
                            <li>Turismo -> Ferrari 488 Pista</li>
                            <li>Elegy -> Nissan GT-R R35 Nismo</li>
                            <li>Bullet -> Ford GT Carbon Edition</li>
                            <li>Sultan -> Subaru WRX STI Rally Spec</li>
                        </ul>
                        <p><strong>Installation:</strong> Place extracted files inside <code>emulated/0/Android_unprotected/data/com.rockstargames.gtasa/mods</code> or <code>files/CLEO</code> or direct in root of game.</p>
                    </div>
                """.trimIndent()
            ),
            ModItem(
                id = "mc_backpacks_expansion",
                title = "Traveler's Backpack & Storage Expansion",
                game = GameCategory.MINECRAFT_BEDROCK,
                category = "Addon (.mcaddon)",
                version = "v2.5.1",
                fileSize = "6.1 MB",
                author = "Bedrock Studio Publisher",
                summary = "Wearable 3D backpacks with crafting tables, fluid storage, sleeping bags, and auto-sorting for survival adventures in Bedrock.",
                blogSpotUrl = "https://gtamc-modhub.blogspot.com/2026/09/minecraft-bedrock-backpacks-addon.html",
                directDownloadUrl = "https://github.com/aistudio-sample/files/raw/main/mods/backpacks_addon_v2.5.1.mcaddon",
                fileExtension = ".mcaddon",
                targetVersion = "Minecraft Bedrock 1.20 - 1.21+",
                bannerDrawableRes = R.drawable.banner_minecraft,
                rating = 4.92f,
                downloadCount = 52300,
                publishDate = "Sep 12, 2026",
                features = listOf(
                    "16 custom dyeable backpack colors with leather & diamond upgrades",
                    "Integrated sleeping bag for camping during nighttime adventures",
                    "Dual fluid tanks: store up to 4 buckets of water or lava",
                    "Wearable on back slot or placeable as a 3D block",
                    "Fully compatible with other survival addons"
                ),
                installSteps = listOf(
                    "Download the .mcaddon file",
                    "Open the file with Minecraft",
                    "Minecraft will import both Behavior and Resource packs",
                    "Edit your world settings > enable 'Holiday Creator Features' & 'Custom Biomes'",
                    "Apply the addon under Behavior & Resource Packs, then click Play"
                ),
                htmlContent = """
                    <div style="font-family: sans-serif; color: #e2e8f0; line-height: 1.6;">
                        <h2 style="color: #10b981;">Traveler's Backpack Addon for MCPE</h2>
                        <p>Never run out of inventory space again! This addon adds fully functional 3D backpacks that attach to your character. Tested on Bedrock 1.21 multiplayer and realms.</p>
                        <h3>Crafting Recipe:</h3>
                        <p>Surround a chest with 8 leather in a crafting table to make the Tier 1 Traveler Backpack. Upgrade with iron, gold, and netherite ingots for massive extra slots!</p>
                    </div>
                """.trimIndent()
            ),
            ModItem(
                id = "gta_60fps_patch",
                title = "60 FPS Unlocker & Physics Overhaul",
                game = GameCategory.GTA_SA,
                category = "Patch & Performance",
                version = "v1.4",
                fileSize = "2.1 MB",
                author = "ModHub Official Editor",
                summary = "Fixes the 30 FPS cap on modern Android displays (90Hz, 120Hz support) and fixes swim/bike speed bugs caused by high framerates.",
                blogSpotUrl = "https://gtamc-modhub.blogspot.com/2026/09/gta-sa-60fps-smooth-patch.html",
                directDownloadUrl = "https://github.com/aistudio-sample/files/raw/main/mods/gta_60fps_patch.zip",
                fileExtension = ".zip",
                targetVersion = "GTA SA Android 2.00+",
                bannerDrawableRes = R.drawable.banner_gta_sa,
                rating = 4.85f,
                downloadCount = 28400,
                publishDate = "Sep 08, 2026",
                features = listOf(
                    "Unlocks 60 FPS and 120 FPS high refresh rates",
                    "Corrects vehicle physics and siren speeds at 60 FPS",
                    "Fixes CJ swimming bug in fast frame mode",
                    "Significantly reduces thermal throttling on Snapdragon and MediaTek"
                ),
                installSteps = listOf(
                    "Extract the patch files",
                    "Copy 'handling.cfg' and 'fps_patch.asi'",
                    "Paste into: emulated/0/Android_unprotected/data/com.rockstargames.gtasa/mods or files/CLEO or direct in game root",
                    "Start GTA SA and toggle Frame Limiter OFF in Game Settings"
                ),
                htmlContent = """
                    <div style="font-family: sans-serif; color: #e2e8f0; line-height: 1.6;">
                        <h2 style="color: #38bdf8;">GTA SA Mobile 60FPS Unlocker</h2>
                        <p>Enjoy silky smooth 60 FPS gameplay! By default, GTA SA on Android is locked to 30 FPS. This patch unlocks high frame rates while preserving normal game physics.</p>
                        <p>Target folder: <code>emulated/0/Android_unprotected/data/com.rockstargames.gtasa/mods</code> or <code>files/CLEO</code> or direct root.</p>
                    </div>
                """.trimIndent()
            ),
            ModItem(
                id = "mc_medieval_dragons",
                title = "Medieval Knights & Tameable Dragons",
                game = GameCategory.MINECRAFT_BEDROCK,
                category = "Addon (.mcaddon)",
                version = "v1.6.0",
                fileSize = "24.2 MB",
                author = "Bedrock Studio Publisher",
                summary = "Breathtaking medieval RPG expansion with tamable flying dragons, fire & ice breath, knight armor, castles, and dungeons.",
                blogSpotUrl = "https://gtamc-modhub.blogspot.com/2026/09/minecraft-bedrock-medieval-dragons.html",
                directDownloadUrl = "https://github.com/aistudio-sample/files/raw/main/mods/medieval_dragons_v1.6.mcaddon",
                fileExtension = ".mcaddon",
                targetVersion = "Minecraft Bedrock 1.20+",
                bannerDrawableRes = R.drawable.banner_minecraft,
                rating = 4.96f,
                downloadCount = 41200,
                publishDate = "Sep 13, 2026",
                features = listOf(
                    "5 different dragon breeds: Fire, Ice, Forest, Lightning, and Ender",
                    "Hatch dragon eggs and feed raw meat to raise your flying companion",
                    "Mount your dragon with a saddle and soar above the clouds",
                    "Castles with NPC knights and custom quest loot"
                ),
                installSteps = listOf(
                    "Tap download (.mcaddon)",
                    "Select Minecraft to import",
                    "Enable Experimental Gameplay toggles in World settings",
                    "Activate both Resource and Behavior packs"
                ),
                htmlContent = """
                    <div style="font-family: sans-serif; color: #e2e8f0; line-height: 1.6;">
                        <h2 style="color: #ef4444;">Medieval Dragons & RPG Overhaul</h2>
                        <p>Embark on an epic fantasy quest with rideable dragons in Minecraft Bedrock! Roam medieval landscapes and conquer bandit dungeons.</p>
                    </div>
                """.trimIndent()
            )
        )
    }
}
