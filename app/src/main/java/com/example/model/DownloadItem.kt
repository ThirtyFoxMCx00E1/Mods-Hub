package com.example.model

data class DownloadItem(
    val downloadId: Long,
    val modId: String,
    val modTitle: String,
    val fileName: String,
    val game: GameCategory,
    val fileSize: String,
    val status: DownloadState,
    val progressPercent: Int = 0,
    val localUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val errorMessage: String? = null
)

enum class DownloadState {
    QUEUED,
    DOWNLOADING,
    COMPLETED,
    FAILED,
    CANCELLED
}
