package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Latency-free synthesized audio engine using Android AudioTrack.
 * Synthesizes retro-cyberpunk combat SFX programmatically in real-time.
 * Eliminates external asset loading delays, crashes, or missing sound files.
 */
class SoundEngine {

    var isSoundEnabled: Boolean = true
    var isBgmEnabled: Boolean = true
    private var isBerserkMode: Boolean = false
    private var bgmJob: kotlinx.coroutines.Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun startDynamicSynthwaveBgm() {
        if (bgmJob != null && bgmJob?.isActive == true) return
        bgmJob = scope.launch {
            val bassNotes = listOf(110.0f, 130.81f, 146.83f, 164.81f) // A2, C3, D3, E3
            var noteIndex = 0
            while (isBgmEnabled) {
                val delayMs = if (isBerserkMode) 220L else 380L
                val freq = bassNotes[noteIndex % bassNotes.size] * (if (isBerserkMode) 1.5f else 1.0f)
                playTone(freq = freq, durationMs = (delayMs * 0.7f).toInt(), amplitude = 0.22f, decay = true)
                noteIndex++
                kotlinx.coroutines.delay(delayMs)
            }
        }
    }

    fun setBerserkBgm(berserk: Boolean) {
        isBerserkMode = berserk
    }

    fun stopBgm() {
        bgmJob?.cancel()
        bgmJob = null
    }

    fun stopDynamicSynthwaveBgm() {
        stopBgm()
    }

    fun playLaserShoot() {
        if (!isSoundEnabled) return
        scope.launch {
            // High to low frequency sweep: 1100Hz down to 220Hz
            playSweep(startFreq = 1100f, endFreq = 220f, durationMs = 120, amplitude = 0.6f)
        }
    }

    fun playBladeSlash() {
        if (!isSoundEnabled) return
        scope.launch {
            // Rapid pitch drop with white noise hiss
            playSweep(startFreq = 850f, endFreq = 180f, durationMs = 90, amplitude = 0.7f)
        }
    }

    fun playShieldBlock() {
        if (!isSoundEnabled) return
        scope.launch {
            // Metallic chime: 1400Hz + 2100Hz overtone with fast exponential decay
            playTone(freq = 1350f, durationMs = 150, amplitude = 0.65f, decay = true)
        }
    }

    fun playCritExplosion() {
        if (!isSoundEnabled) return
        scope.launch {
            // Low rumble bass boom: 130Hz -> 50Hz with saturation
            playSweep(startFreq = 140f, endFreq = 45f, durationMs = 280, amplitude = 0.9f)
        }
    }

    fun playEmpShockwave() {
        if (!isSoundEnabled) return
        scope.launch {
            // Oscillating zap
            playSweep(startFreq = 400f, endFreq = 950f, durationMs = 180, amplitude = 0.75f)
        }
    }

    fun playOverdriveUltimate() {
        if (!isSoundEnabled) return
        scope.launch {
            // Ascending power-up chord sweep
            playSweep(startFreq = 200f, endFreq = 1600f, durationMs = 350, amplitude = 0.85f)
        }
    }

    fun playUiBlip() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(freq = 880f, durationMs = 45, amplitude = 0.35f)
        }
    }

    fun playLevelUpFanfare() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(freq = 523.25f, durationMs = 80, amplitude = 0.5f) // C5
            playTone(freq = 659.25f, durationMs = 80, amplitude = 0.5f) // E5
            playTone(freq = 783.99f, durationMs = 140, amplitude = 0.6f) // G5
        }
    }

    fun playVictoryFanfare() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(freq = 440.0f, durationMs = 100, amplitude = 0.5f) // A4
            playTone(freq = 554.37f, durationMs = 100, amplitude = 0.5f) // C#5
            playTone(freq = 659.25f, durationMs = 120, amplitude = 0.6f) // E5
            playTone(freq = 880.0f, durationMs = 200, amplitude = 0.7f) // A5
        }
    }

    private fun playTone(freq: Float, durationMs: Int, amplitude: Float, decay: Boolean = false) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val time = i.toDouble() / sampleRate
                val decayFactor = if (decay) (1.0 - (i.toDouble() / numSamples)) else 1.0
                val angle = 2.0 * PI * freq * time
                val sampleVal = (sin(angle) * Short.MAX_VALUE * amplitude * decayFactor).toInt()
                buffer[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            writeAudio(sampleRate, buffer)
        } catch (_: Exception) {
            // Fail safely without crashing
        }
    }

    private fun playSweep(startFreq: Float, endFreq: Float, durationMs: Int, amplitude: Float) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * progress
                val time = i.toDouble() / sampleRate
                val angle = 2.0 * PI * currentFreq * time
                val decay = 1.0 - progress * 0.4
                val sampleVal = (sin(angle) * Short.MAX_VALUE * amplitude * decay).toInt()
                buffer[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            writeAudio(sampleRate, buffer)
        } catch (_: Exception) {
            // Fail safely without crashing
        }
    }

    private fun writeAudio(sampleRate: Int, buffer: ShortArray) {
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()
        audioTrack.setNotificationMarkerPosition(buffer.size)
        audioTrack.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
            override fun onMarkerReached(track: AudioTrack?) {
                track?.stop()
                track?.release()
            }
            override fun onPeriodicNotification(track: AudioTrack?) {}
        })
    }
}
