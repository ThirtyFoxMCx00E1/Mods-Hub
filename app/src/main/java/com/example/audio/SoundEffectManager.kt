package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages low-latency UI click feedback sound ("Mystery Alert" click sound)
 * for buttons, navigation tabs, cards, and interactive elements.
 */
class SoundEffectManager(private val context: Context) {

    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0
    private var isLoaded: Boolean = false

    private val prefs = context.getSharedPreferences("modhub_audio_prefs", Context.MODE_PRIVATE)

    private val _isSoundEnabled = MutableStateFlow(prefs.getBoolean("sound_effects_enabled", true))
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    init {
        initSoundPool()
    }

    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build().apply {
                setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0 && sampleId == clickSoundId) {
                        isLoaded = true
                    }
                }
            }

        clickSoundId = soundPool?.load(context, R.raw.click_sound, 1) ?: 0
    }

    fun playClick() {
        if (!_isSoundEnabled.value) return
        if (isLoaded && soundPool != null) {
            soundPool?.play(clickSoundId, 0.9f, 0.9f, 1, 0, 1.0f)
        }
    }

    fun toggleSoundEnabled() {
        val next = !_isSoundEnabled.value
        _isSoundEnabled.value = next
        prefs.edit().putBoolean("sound_effects_enabled", next).apply()
        if (next) {
            playClick()
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        isLoaded = false
    }
}
