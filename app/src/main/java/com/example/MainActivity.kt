package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.LobbyMusicManager
import com.example.audio.SoundEffectManager
import com.example.data.BlogspotModRepository
import com.example.data.DownloadHelper
import com.example.data.NetworkHelper
import com.example.model.DownloadState
import com.example.model.GameCategory
import com.example.model.ModItem
import com.example.ui.components.BlogspotSourceDialog
import com.example.ui.components.CustomPathDialog
import com.example.ui.components.DownloadsSheet
import com.example.ui.components.LobbyMusicDialog
import com.example.ui.components.OfflineNoticeDialog
import com.example.ui.components.PublisherOnlyNoticeDialog
import com.example.ui.components.SponsoredAdDialog
import com.example.ui.components.VersionUpdateDialog
import com.example.ui.screens.BlogFeedScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InstallGuideScreen
import com.example.ui.screens.ModDetailScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.VolumeUp

class MainActivity : ComponentActivity() {

  private val repository = BlogspotModRepository()
  private lateinit var downloadHelper: DownloadHelper
  private lateinit var soundEffectManager: SoundEffectManager
  private lateinit var lobbyMusicManager: LobbyMusicManager
  private lateinit var networkHelper: NetworkHelper

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    downloadHelper = DownloadHelper(applicationContext)
    soundEffectManager = SoundEffectManager(applicationContext)
    lobbyMusicManager = LobbyMusicManager(applicationContext)
    networkHelper = NetworkHelper(applicationContext)

    // Automatically start the 2h looping lobby music
    lobbyMusicManager.play()

    setContent {
      MyApplicationTheme {
        ModHubApp(
          repository = repository,
          downloadHelper = downloadHelper,
          soundEffectManager = soundEffectManager,
          lobbyMusicManager = lobbyMusicManager,
          networkHelper = networkHelper
        )
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    lobbyMusicManager.release()
    soundEffectManager.release()
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModHubApp(
  repository: BlogspotModRepository,
  downloadHelper: DownloadHelper,
  soundEffectManager: SoundEffectManager,
  lobbyMusicManager: LobbyMusicManager,
  networkHelper: NetworkHelper,
  modifier: Modifier = Modifier
) {
  var showSplashScreen by remember { mutableStateOf(true) }
  var selectedTab by remember { mutableIntStateOf(0) }
  var selectedGameFilter by remember { mutableStateOf<GameCategory?>(null) }
  var selectedMod by remember { mutableStateOf<ModItem?>(null) }
  var showPublisherDialog by remember { mutableStateOf(false) }
  var showBlogspotSourceDialog by remember { mutableStateOf(false) }
  var showDownloadsSheet by remember { mutableStateOf(false) }
  var showVersionUpdateDialog by remember { mutableStateOf(false) }
  var showCustomPathDialog by remember { mutableStateOf(false) }
  var showLobbyMusicDialog by remember { mutableStateOf(false) }
  var showOfflineNoticeDialog by remember { mutableStateOf(false) }
  var showSponsoredAdDialog by remember { mutableStateOf(false) }
  var onlineActionCount by remember { mutableIntStateOf(0) }

  val isOnline by networkHelper.isOnline.collectAsState()
  val mods = remember { repository.getMods() }
  val bookmarkedIds by repository.bookmarkedIds.collectAsState()
  val blogspotSource by repository.currentBlogspotSource.collectAsState()
  val downloads by downloadHelper.downloads.collectAsState()
  val isMusicPlaying by lobbyMusicManager.isPlaying.collectAsState()

  val activeDownloadsCount = downloads.count { it.status == DownloadState.DOWNLOADING }

  val onDownloadRequest: (ModItem) -> Unit = { mod ->
    soundEffectManager.playClick()
    if (!isOnline) {
      showOfflineNoticeDialog = true
    } else {
      onlineActionCount++
      if (onlineActionCount % 3 == 0) {
        showSponsoredAdDialog = true
      }
      downloadHelper.startDownload(mod)
    }
  }

  if (showSplashScreen) {
    SplashScreen(
      isOnline = isOnline,
      onFinishSplash = { showSplashScreen = false }
    )
    return
  }

  // Handle system back navigation when Mod Detail is active
  BackHandler(enabled = selectedMod != null) {
    soundEffectManager.playClick()
    selectedMod = null
  }

  if (selectedMod != null) {
    ModDetailScreen(
      mod = selectedMod!!,
      isBookmarked = bookmarkedIds.contains(selectedMod!!.id),
      customPathManager = downloadHelper.customPathManager,
      soundEffectManager = soundEffectManager,
      onBack = {
        soundEffectManager.playClick()
        selectedMod = null
      },
      onDownload = onDownloadRequest,
      onToggleBookmark = { id ->
        soundEffectManager.playClick()
        repository.toggleBookmark(id)
      },
      onOpenCustomPath = {
        soundEffectManager.playClick()
        showCustomPathDialog = true
      }
    )
  } else {
    Scaffold(
      modifier = modifier.fillMaxSize(),
      containerColor = ObsidianBg,
      topBar = {
        TopAppBar(
          title = {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(34.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(CyanAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Explore,
                  contentDescription = null,
                  tint = CyanAccent,
                  modifier = Modifier.size(20.dp)
                )
              }
              Column {
                Text(
                  text = "Mod Hub",
                  color = TextPrimary,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 17.sp
                )
                Text(
                  text = "GTA SA & Minecraft Bedrock",
                  color = TextMuted,
                  fontSize = 11.sp
                )
              }
            }
          },
          actions = {
            // Lobby Music Quick Dialog Action
            IconButton(
              onClick = {
                soundEffectManager.playClick()
                showLobbyMusicDialog = true
              },
              modifier = Modifier.testTag("lobby_music_action")
            ) {
              Icon(
                imageVector = if (isMusicPlaying) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.MusicNote,
                contentDescription = "Lobby Music",
                tint = if (isMusicPlaying) Color(0xFF10B981) else TextSecondary
              )
            }

            // Custom Folder Path Quick Action
            IconButton(
              onClick = {
                soundEffectManager.playClick()
                showCustomPathDialog = true
              },
              modifier = Modifier.testTag("custom_path_action")
            ) {
              Icon(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = "Custom Download Path",
                tint = CyanAccent
              )
            }

            // Version 1.3 Update Status Action
            IconButton(
              onClick = {
                soundEffectManager.playClick()
                showVersionUpdateDialog = true
              },
              modifier = Modifier.testTag("update_action_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Version 1.3",
                tint = Color(0xFF38BDF8)
              )
            }

            // Publisher Policy info icon button
            IconButton(
              onClick = {
                soundEffectManager.playClick()
                showPublisherDialog = true
              },
              modifier = Modifier.testTag("publisher_info_action")
            ) {
              Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = "Publisher Info",
                tint = CyanAccent
              )
            }

            // Blogspot URL / source switcher
            IconButton(
              onClick = {
                soundEffectManager.playClick()
                showBlogspotSourceDialog = true
              },
              modifier = Modifier.testTag("source_switcher_action")
            ) {
              Icon(
                imageVector = Icons.Default.Language,
                contentDescription = "Blogspot Source",
                tint = TextSecondary
              )
            }

            // Downloads Sheet Action with badge
            IconButton(
              onClick = {
                soundEffectManager.playClick()
                showDownloadsSheet = true
              },
              modifier = Modifier.testTag("open_downloads_action")
            ) {
              BadgedBox(
                badge = {
                  if (activeDownloadsCount > 0 || downloads.isNotEmpty()) {
                    Badge(containerColor = CyanAccent) {
                      Text(
                        text = if (activeDownloadsCount > 0) "$activeDownloadsCount" else "${downloads.size}",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
              ) {
                Icon(
                  imageVector = Icons.Default.Download,
                  contentDescription = "Downloads",
                  tint = if (activeDownloadsCount > 0) CyanAccent else TextPrimary
                )
              }
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SlateCard
          )
        )
      },
      bottomBar = {
        NavigationBar(
          containerColor = SlateCard,
          modifier = Modifier.navigationBarsPadding().testTag("bottom_navigation")
        ) {
          NavigationBarItem(
            selected = selectedTab == 0,
            onClick = {
              soundEffectManager.playClick()
              selectedTab = 0
            },
            icon = {
              Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = "Mods Catalog"
              )
            },
            label = { Text("Mods", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.Black,
              indicatorColor = CyanAccent,
              selectedTextColor = CyanAccent,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_explore")
          )

          NavigationBarItem(
            selected = selectedTab == 1,
            onClick = {
              soundEffectManager.playClick()
              selectedTab = 1
            },
            icon = {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Article,
                contentDescription = "Blogspot Feed"
              )
            },
            label = { Text("Blogspot HTML", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.Black,
              indicatorColor = CyanAccent,
              selectedTextColor = CyanAccent,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_blog")
          )

          NavigationBarItem(
            selected = selectedTab == 2,
            onClick = {
              soundEffectManager.playClick()
              selectedTab = 2
            },
            icon = {
              Icon(
                imageVector = Icons.Default.Build,
                contentDescription = "Install Manual"
              )
            },
            label = { Text("Install Guide", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.Black,
              indicatorColor = CyanAccent,
              selectedTextColor = CyanAccent,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_guide")
          )
        }
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when (selectedTab) {
          0 -> HomeScreen(
            mods = mods,
            bookmarkedIds = bookmarkedIds,
            selectedGame = selectedGameFilter,
            customPathManager = downloadHelper.customPathManager,
            lobbyMusicManager = lobbyMusicManager,
            soundEffectManager = soundEffectManager,
            isOnline = isOnline,
            onOpenOfflineNotice = { showOfflineNoticeDialog = true },
            onSelectGame = {
              soundEffectManager.playClick()
              selectedGameFilter = it
            },
            onModClick = {
              soundEffectManager.playClick()
              selectedMod = it
            },
            onDownloadClick = onDownloadRequest,
            onToggleBookmark = { id ->
              soundEffectManager.playClick()
              repository.toggleBookmark(id)
            },
            onOpenPublisherInfo = {
              soundEffectManager.playClick()
              showPublisherDialog = true
            },
            onOpenCustomPath = {
              soundEffectManager.playClick()
              showCustomPathDialog = true
            },
            onOpenLobbyMusic = {
              soundEffectManager.playClick()
              showLobbyMusicDialog = true
            }
          )
          1 -> BlogFeedScreen(
            mods = mods,
            currentSource = blogspotSource,
            isOnline = isOnline,
            onOpenOfflineNotice = { showOfflineNoticeDialog = true },
            onSelectMod = {
              soundEffectManager.playClick()
              selectedMod = it
            },
            onChangeSource = {
              soundEffectManager.playClick()
              showBlogspotSourceDialog = true
            }
          )
          2 -> InstallGuideScreen(
            customPathManager = downloadHelper.customPathManager,
            soundEffectManager = soundEffectManager,
            onOpenCustomPath = {
              soundEffectManager.playClick()
              showCustomPathDialog = true
            }
          )
        }
      }
    }
  }

  // Offline Notice Dialog
  if (showOfflineNoticeDialog) {
    OfflineNoticeDialog(
      onRetryConnection = {
        soundEffectManager.playClick()
        networkHelper.refresh()
        if (networkHelper.checkIsOnline()) {
          showOfflineNoticeDialog = false
        }
      },
      onContinueOffline = {
        soundEffectManager.playClick()
        showOfflineNoticeDialog = false
      },
      onDismiss = {
        soundEffectManager.playClick()
        showOfflineNoticeDialog = false
      }
    )
  }

  // Sponsored Ad Dialog (appears sometimes when using online)
  if (showSponsoredAdDialog) {
    SponsoredAdDialog(
      onDismiss = {
        soundEffectManager.playClick()
        showSponsoredAdDialog = false
      }
    )
  }

  // Publisher-Only Explanation Dialog
  if (showPublisherDialog) {
    PublisherOnlyNoticeDialog(
      onDismiss = {
        soundEffectManager.playClick()
        showPublisherDialog = false
      }
    )
  }

  // Blogspot Domain / Source URL Dialog
  if (showBlogspotSourceDialog) {
    BlogspotSourceDialog(
      currentSource = blogspotSource,
      onSaveSource = { newUrl ->
        soundEffectManager.playClick()
        repository.updateBlogspotSource(newUrl)
      },
      onDismiss = {
        soundEffectManager.playClick()
        showBlogspotSourceDialog = false
      }
    )
  }

  // Version 1.3 Status & Update Check Dialog
  if (showVersionUpdateDialog) {
    VersionUpdateDialog(
      onDismiss = {
        soundEffectManager.playClick()
        showVersionUpdateDialog = false
      },
      onOpenCustomPath = {
        showVersionUpdateDialog = false
        showCustomPathDialog = true
      },
      onOpenAudioSettings = {
        showVersionUpdateDialog = false
        showLobbyMusicDialog = true
      },
      onPlayClick = { soundEffectManager.playClick() }
    )
  }

  // Custom Path Configuration Dialog
  if (showCustomPathDialog) {
    CustomPathDialog(
      customPathManager = downloadHelper.customPathManager,
      onDismiss = {
        soundEffectManager.playClick()
        showCustomPathDialog = false
      },
      onPlayClick = { soundEffectManager.playClick() }
    )
  }

  // Lobby Music & Sound Effect Controls Dialog
  if (showLobbyMusicDialog) {
    LobbyMusicDialog(
      lobbyMusicManager = lobbyMusicManager,
      soundEffectManager = soundEffectManager,
      onDismiss = {
        soundEffectManager.playClick()
        showLobbyMusicDialog = false
      }
    )
  }

  // Downloads Modal Bottom Sheet
  if (showDownloadsSheet) {
    ModalBottomSheet(
      onDismissRequest = {
        soundEffectManager.playClick()
        showDownloadsSheet = false
      },
      containerColor = SlateCard,
      shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
      DownloadsSheet(
        downloads = downloads,
        onOpenItem = { item ->
          soundEffectManager.playClick()
          downloadHelper.openDownloadedMod(item)
        },
        onDeleteItem = { id ->
          soundEffectManager.playClick()
          downloadHelper.removeDownload(id)
        },
        onConfigurePath = {
          soundEffectManager.playClick()
          showDownloadsSheet = false
          showCustomPathDialog = true
        },
        onClose = {
          soundEffectManager.playClick()
          showDownloadsSheet = false
        }
      )
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Mod Hub - $name", modifier = modifier)
}

