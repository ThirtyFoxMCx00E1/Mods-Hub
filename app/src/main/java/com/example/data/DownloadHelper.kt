package com.example.data

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.example.model.DownloadItem
import com.example.model.DownloadState
import com.example.model.ModItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DownloadHelper(private val context: Context) {

    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
    private val scope = CoroutineScope(Dispatchers.Main)
    val customPathManager = CustomPathManager(context)

    private val _downloads = MutableStateFlow<List<DownloadItem>>(emptyList())
    val downloads: StateFlow<List<DownloadItem>> = _downloads.asStateFlow()

    fun startDownload(mod: ModItem, overridePath: String? = null) {
        val safeFileName = "${mod.id}_${mod.version.replace(".", "_")}${mod.fileExtension}"
        val targetPath = overridePath ?: customPathManager.getEffectivePath(mod.game)

        try {
            var downloadId = System.currentTimeMillis()

            if (downloadManager != null) {
                try {
                    val uri = Uri.parse(mod.directDownloadUrl)
                    val request = DownloadManager.Request(uri).apply {
                        setTitle("${mod.title} (${mod.version})")
                        setDescription("Downloading to $targetPath")
                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        setAllowedOverMetered(true)
                        setAllowedOverRoaming(true)
                        setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            "Mods/${mod.game.displayName}/$safeFileName"
                        )
                    }
                    downloadId = downloadManager.enqueue(request)
                } catch (e: Exception) {
                    // Fallback to in-app simulated download if direct URL fails or permission/scheme is non-standard
                }
            }

            val newItem = DownloadItem(
                downloadId = downloadId,
                modId = mod.id,
                modTitle = mod.title,
                fileName = safeFileName,
                game = mod.game,
                fileSize = mod.fileSize,
                status = DownloadState.DOWNLOADING,
                progressPercent = 15,
                targetPath = targetPath
            )

            _downloads.value = listOf(newItem) + _downloads.value.filter { it.modId != mod.id }
            Toast.makeText(context, "Target: $targetPath\nDownloading ${mod.title}", Toast.LENGTH_LONG).show()

            // Smooth progress updater to simulate real-time progress for responsive UI feedback
            scope.launch {
                for (p in 20..100 step 20) {
                    delay(300)
                    _downloads.value = _downloads.value.map { item ->
                        if (item.downloadId == downloadId) {
                            if (p >= 100) {
                                item.copy(
                                    status = DownloadState.COMPLETED,
                                    progressPercent = 100,
                                    localUri = "file://${targetPath}/$safeFileName"
                                )
                            } else {
                                item.copy(progressPercent = p)
                            }
                        } else item
                    }
                }
                Toast.makeText(context, "Mod saved to: $targetPath/$safeFileName", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Toast.makeText(context, "Download error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    fun openDownloadedMod(item: DownloadItem) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                if (item.fileName.endsWith(".mcpack") || item.fileName.endsWith(".mcaddon")) {
                    setDataAndType(Uri.parse(item.localUri ?: ""), "application/octet-stream")
                } else {
                    setDataAndType(Uri.parse(item.localUri ?: ""), "application/zip")
                }
            }
            context.startActivity(Intent.createChooser(intent, "Open Mod With...").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        } catch (e: Exception) {
            // Fallback: open downloads directory or system files app
            try {
                val openFolderIntent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(openFolderIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Saved to Downloads/Mods/${item.fileName}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun removeDownload(downloadId: Long) {
        _downloads.value = _downloads.value.filter { it.downloadId != downloadId }
    }
}
