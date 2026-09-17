package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Manages background 2-hour looping lobby music.
 * Can play the bundled chill lo-fi loop or load a custom local audio file
 * (like videoplayback (2).m4a uploaded by user) from device storage.
 */
class LobbyMusicManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var progressJob: Job? = null

    private val prefs = context.getSharedPreferences("modhub_audio_prefs", Context.MODE_PRIVATE)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isLooping = MutableStateFlow(prefs.getBoolean("lobby_loop_enabled", true))
    val isLooping: StateFlow<Boolean> = _isLooping.asStateFlow()

    private val _volume = MutableStateFlow(prefs.getFloat("lobby_music_volume", 0.7f))
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private val _trackTitle = MutableStateFlow(
        prefs.getString("lobby_track_title", "videoplayback (2).m4a (2h Lobby Loop)") ?: "videoplayback (2).m4a (2h Lobby Loop)"
    )
    val trackTitle: StateFlow<String> = _trackTitle.asStateFlow()

    private val _isCustomTrack = MutableStateFlow(prefs.getBoolean("is_custom_track", false))
    val isCustomTrack: StateFlow<Boolean> = _isCustomTrack.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0)
    val currentPositionMs: StateFlow<Int> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0)
    val durationMs: StateFlow<Int> = _durationMs.asStateFlow()

    init {
        initMediaPlayer()
    }

    private fun initMediaPlayer(customUri: Uri? = null) {
        try {
            mediaPlayer?.release()
            mediaPlayer = null

            val player = if (customUri != null) {
                MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(context, customUri)
                    prepare()
                }
            } else {
                MediaPlayer.create(context, R.raw.lobby_music)?.apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                }
            }

            if (player != null) {
                player.isLooping = _isLooping.value
                val vol = _volume.value
                player.setVolume(vol, vol)
                _durationMs.value = player.duration

                player.setOnCompletionListener {
                    if (!_isLooping.value) {
                        _isPlaying.value = false
                    }
                }
                player.setOnErrorListener { _, _, _ ->
                    _isPlaying.value = false
                    false
                }
                mediaPlayer = player
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun play() {
        try {
            if (mediaPlayer == null) {
                initMediaPlayer()
            }
            mediaPlayer?.start()
            _isPlaying.value = true
            startProgressTracker()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pause() {
        try {
            mediaPlayer?.pause()
            _isPlaying.value = false
            progressJob?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }

    fun setLooping(enabled: Boolean) {
        _isLooping.value = enabled
        mediaPlayer?.isLooping = enabled
        prefs.edit().putBoolean("lobby_loop_enabled", enabled).apply()
    }

    fun setVolume(newVolume: Float) {
        val clamped = newVolume.coerceIn(0f, 1f)
        _volume.value = clamped
        mediaPlayer?.setVolume(clamped, clamped)
        prefs.edit().putFloat("lobby_music_volume", clamped).apply()
    }

    fun setCustomAudioUri(uri: Uri, title: String) {
        try {
            _trackTitle.value = title
            _isCustomTrack.value = true
            prefs.edit()
                .putString("lobby_track_title", title)
                .putBoolean("is_custom_track", true)
                .putString("custom_audio_uri", uri.toString())
                .apply()

            initMediaPlayer(uri)
            play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resetToDefaultLobbyMusic() {
        _isCustomTrack.value = false
        _trackTitle.value = "videoplayback (2).m4a (2h Lobby Loop)"
        prefs.edit()
            .putBoolean("is_custom_track", false)
            .putString("lobby_track_title", "videoplayback (2).m4a (2h Lobby Loop)")
            .remove("custom_audio_uri")
            .apply()

        initMediaPlayer(null)
        play()
    }

    fun seekTo(positionMs: Int) {
        try {
            mediaPlayer?.seekTo(positionMs)
            _currentPositionMs.value = positionMs
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive && _isPlaying.value) {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        _currentPositionMs.value = player.currentPosition
                        _durationMs.value = player.duration
                    }
                }
                delay(500)
            }
        }
    }

    fun release() {
        progressJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
    }
}
