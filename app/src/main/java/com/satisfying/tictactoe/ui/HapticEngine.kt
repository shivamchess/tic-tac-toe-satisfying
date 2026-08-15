package com.satisfying.tictactoe.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object HapticEngine {

    /**
     * Light crisp tap — fires on every cell press
     */
    fun tap(context: Context) {
        vibrate(context, longArrayOf(0, 18), intArrayOf(0, 180))
    }

    /**
     * Heavy thud — fires when a winning move is placed
     */
    fun heavyImpact(context: Context) {
        vibrate(context, longArrayOf(0, 60, 40, 30), intArrayOf(0, 255, 0, 200))
    }

    /**
     * Triple pulse — fires on DRAW
     */
    fun drawPulse(context: Context) {
        vibrate(context, longArrayOf(0, 30, 60, 30, 60, 30), intArrayOf(0, 150, 0, 150, 0, 150))
    }

    /**
     * Victory rumble — long satisfying buzz for win celebration
     */
    fun victoryRumble(context: Context) {
        vibrate(
            context,
            longArrayOf(0, 80, 40, 60, 40, 100, 30, 200),
            intArrayOf(0, 200, 0, 180, 0, 220, 0, 255)
        )
    }

    /**
     * Soft click — for button navigation
     */
    fun click(context: Context) {
        vibrate(context, longArrayOf(0, 10), intArrayOf(0, 120))
    }

    private fun vibrate(context: Context, timings: LongArray, amplitudes: IntArray) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator?.vibrate(
                    VibrationEffect.createWaveform(timings, amplitudes, -1)
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(timings, -1)
            }
        } catch (_: Exception) {
            // Silently ignore if no vibrator available
        }
    }
}
