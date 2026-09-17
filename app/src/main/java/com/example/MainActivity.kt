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
import com.example.data.BlogspotModRepository
import com.example.data.DownloadHelper
import com.example.model.DownloadState
import com.example.model.GameCategory
import com.example.model.ModItem
import com.example.ui.components.BlogspotSourceDialog
import com.example.ui.components.DownloadsSheet
import com.example.ui.components.PublisherOnlyNoticeDialog
import com.example.ui.components.VersionUpdateDialog
import com.example.ui.screens.BlogFeedScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InstallGuideScreen
import com.example.ui.screens.ModDetailScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {

  private val repository = BlogspotModRepository()
  private lateinit var downloadHelper: DownloadHelper

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    downloadHelper = DownloadHelper(applicationContext)

    setContent {
      MyApplicationTheme {
        ModHubApp(
          repository = repository,
          downloadHelper = downloadHelper
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModHubApp(
  repository: BlogspotModRepository,
  downloadHelper: DownloadHelper,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var selectedGameFilter by remember { mutableStateOf<GameCategory?>(null) }
  var selectedMod by remember { mutableStateOf<ModItem?>(null) }
  var showPublisherDialog by remember { mutableStateOf(false) }
  var showBlogspotSourceDialog by remember { mutableStateOf(false) }
  var showDownloadsSheet by remember { mutableStateOf(false) }
  var showVersionUpdateDialog by remember { mutableStateOf(false) }

  val mods = remember { repository.getMods() }
  val bookmarkedIds by repository.bookmarkedIds.collectAsState()
  val blogspotSource by repository.currentBlogspotSource.collectAsState()
  val downloads by downloadHelper.downloads.collectAsState()

  val activeDownloadsCount = downloads.count { it.status == DownloadState.DOWNLOADING }

  // Handle system back navigation when Mod Detail is active
  BackHandler(enabled = selectedMod != null) {
    selectedMod = null
  }

  if (selectedMod != null) {
    ModDetailScreen(
      mod = selectedMod!!,
      isBookmarked = bookmarkedIds.contains(selectedMod!!.id),
      onBack = { selectedMod = null },
      onDownload = { mod ->
        downloadHelper.startDownload(mod)
      },
      onToggleBookmark = { id ->
        repository.toggleBookmark(id)
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text(
                    text = "Mod Hub",
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp
                  )
                  Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                      .clickable { showVersionUpdateDialog = true }
                      .testTag("version_pill")
                  ) {
                    Text(
                      text = "v1.0 Ready",
                      color = CyanAccent,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = "GTA SA & Minecraft Bedrock",
                  color = TextMuted,
                  fontSize = 11.sp
                )
              }
            }
          },
          actions = {
            // Version 1.0 Update Status Action
            IconButton(
              onClick = { showVersionUpdateDialog = true },
              modifier = Modifier.testTag("update_action_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Version 1.0",
                tint = Color(0xFF38BDF8)
              )
            }

            // Publisher Policy info icon button
            IconButton(
              onClick = { showPublisherDialog = true },
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
              onClick = { showBlogspotSourceDialog = true },
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
              onClick = { showDownloadsSheet = true },
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
            onClick = { selectedTab = 0 },
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
            onClick = { selectedTab = 1 },
            icon = {
              Icon(
                imageVector = Icons.Default.Article,
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
            onClick = { selectedTab = 2 },
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
            onSelectGame = { selectedGameFilter = it },
            onModClick = { selectedMod = it },
            onDownloadClick = { mod ->
              downloadHelper.startDownload(mod)
            },
            onToggleBookmark = { id ->
              repository.toggleBookmark(id)
            },
            onOpenPublisherInfo = {
              showPublisherDialog = true
            }
          )
          1 -> BlogFeedScreen(
            mods = mods,
            currentSource = blogspotSource,
            onSelectMod = { selectedMod = it },
            onChangeSource = { showBlogspotSourceDialog = true }
          )
          2 -> InstallGuideScreen()
        }
      }
    }
  }

  // Publisher-Only Explanation Dialog
  if (showPublisherDialog) {
    PublisherOnlyNoticeDialog(
      onDismiss = { showPublisherDialog = false }
    )
  }

  // Blogspot Domain / Source URL Dialog
  if (showBlogspotSourceDialog) {
    BlogspotSourceDialog(
      currentSource = blogspotSource,
      onSaveSource = { newUrl ->
        repository.updateBlogspotSource(newUrl)
      },
      onDismiss = { showBlogspotSourceDialog = false }
    )
  }

  // Version 1.0 Status & Update Check Dialog
  if (showVersionUpdateDialog) {
    VersionUpdateDialog(
      onDismiss = { showVersionUpdateDialog = false }
    )
  }

  // Downloads Modal Bottom Sheet
  if (showDownloadsSheet) {
    ModalBottomSheet(
      onDismissRequest = { showDownloadsSheet = false },
      containerColor = SlateCard,
      shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
      DownloadsSheet(
        downloads = downloads,
        onOpenItem = { item ->
          downloadHelper.openDownloadedMod(item)
        },
        onDeleteItem = { id ->
          downloadHelper.removeDownload(id)
        },
        onClose = { showDownloadsSheet = false }
      )
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Mod Hub - $name", modifier = modifier)
}

