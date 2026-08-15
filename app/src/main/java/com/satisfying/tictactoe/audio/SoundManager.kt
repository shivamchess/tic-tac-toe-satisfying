package com.satisfying.tictactoe.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundManager {
    private var isMuted = false
    private val scope = CoroutineScope(Dispatchers.Default)

    fun toggleMute(): Boolean {
        isMuted = !isMuted
        return isMuted
    }

    fun isAudioMuted(): Boolean = isMuted

    // Play a juicy bubble pop with rising pitch per move
    fun playPop(moveCount: Int) {
        if (isMuted) return
        scope.launch {
            // Pentatonic scale base frequencies: C4, D4, E4, G4, A4, C5, D5, E5, G5
            val pentatonic = doubleArrayOf(261.63, 293.66, 329.63, 392.00, 440.00, 523.25, 587.33, 659.25, 783.99)
            val baseFreq = pentatonic[moveCount.coerceIn(0, pentatonic.size - 1)]
            generateAndPlayTone(
                startFreq = baseFreq,
                endFreq = baseFreq * 1.5,
                durationMs = 90,
                volume = 0.85f,
                waveType = WaveType.SINE
            )
        }
    }

    // Play a rewarding victory chord arpeggio
    fun playWin() {
        if (isMuted) return
        scope.launch {
            val chord = doubleArrayOf(523.25, 659.25, 783.99, 1046.50) // C Major triad + octave
            chord.forEachIndexed { index, freq ->
                generateAndPlayTone(
                    startFreq = freq,
                    endFreq = freq,
                    durationMs = 180 + (index * 40),
                    volume = 0.75f,
                    waveType = WaveType.TRIANGLE
                )
                kotlinx.coroutines.delay(70)
            }
        }
    }

    // Play a gentle thud on draw
    fun playDraw() {
        if (isMuted) return
        scope.launch {
            generateAndPlayTone(
                startFreq = 220.0,
                endFreq = 110.0,
                durationMs = 150,
                volume = 0.6f,
                waveType = WaveType.SINE
            )
        }
    }

    // Play click for buttons
    fun playClick() {
        if (isMuted) return
        scope.launch {
            generateAndPlayTone(
                startFreq = 800.0,
                endFreq = 400.0,
                durationMs = 40,
                volume = 0.5f,
                waveType = WaveType.SINE
            )
        }
    }

    private enum class WaveType { SINE, TRIANGLE }

    private fun generateAndPlayTone(
        startFreq: Double,
        endFreq: Double,
        durationMs: Int,
        volume: Float,
        waveType: WaveType
    ) {
        try {
            val sampleRate = 44100
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            var currentPhase = 0.0
            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * progress
                val phaseIncrement = 2.0 * Math.PI * currentFreq / sampleRate
                currentPhase += phaseIncrement

                // ADSR envelope: Fast attack, exponential decay
                val envelope = when {
                    progress < 0.1 -> progress / 0.1
                    else -> Math.exp(-progress * 4.0)
                }

                val sample = when (waveType) {
                    WaveType.SINE -> sin(currentPhase)
                    WaveType.TRIANGLE -> (2.0 / Math.PI) * Math.asin(sin(currentPhase))
                }

                val shortVal = (sample * envelope * volume * Short.MAX_VALUE).toInt()
                buffer[i] = shortVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

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

            // Release after playing
            scope.launch {
                kotlinx.coroutines.delay(durationMs.toLong() + 50)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            // AudioTrack failure fallback gracefully
        }
    }
}
