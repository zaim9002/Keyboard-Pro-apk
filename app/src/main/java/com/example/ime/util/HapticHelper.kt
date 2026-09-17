package com.example.ime.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View

object HapticHelper {

    private var cachedVibrator: Vibrator? = null
    private var isVibratorInitialized = false
    private var hasVibratorHardware = true

    private fun getVibrator(context: Context?): Vibrator? {
        if (!isVibratorInitialized && context != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    cachedVibrator = vibratorManager?.defaultVibrator
                }
                if (cachedVibrator == null) {
                    @Suppress("DEPRECATION")
                    cachedVibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                }
                hasVibratorHardware = cachedVibrator?.hasVibrator() ?: false
                isVibratorInitialized = true
            } catch (e: Throwable) {
                isVibratorInitialized = true
                hasVibratorHardware = false
            }
        }
        return cachedVibrator
    }

    fun performKeyHaptic(context: Context?, view: View?, intensity: String = "Medium") {
        if (intensity.equals("Off", ignoreCase = true)) return

        try {
            val vibrator = getVibrator(context)
            val hasVib = hasVibratorHardware && vibrator != null

            when (intensity.lowercase()) {
                "light" -> {
                    val performed = view?.performHapticFeedback(
                        HapticFeedbackConstants.KEYBOARD_TAP,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                    ) ?: false
                    if (!performed && hasVib) {
                        vibrateMillis(vibrator, 8L, 80)
                    }
                }
                "strong" -> {
                    var performed = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        performed = view?.performHapticFeedback(
                            HapticFeedbackConstants.CONFIRM,
                            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                        ) ?: false
                    }
                    if (!performed) {
                        performed = view?.performHapticFeedback(
                            HapticFeedbackConstants.LONG_PRESS,
                            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                        ) ?: false
                    }
                    if (hasVib) {
                        vibrateMillis(vibrator, 35L, 255)
                    }
                }
                else -> { // "medium"
                    val performed = view?.performHapticFeedback(
                        HapticFeedbackConstants.KEYBOARD_TAP,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                    ) ?: false
                    if (!performed && hasVib) {
                        vibrateMillis(vibrator, 18L, 160)
                    }
                }
            }
        } catch (e: Throwable) {
            // Ignored for safety
        }
    }

    private fun vibrateMillis(vibrator: Vibrator?, millis: Long, amplitude: Int) {
        if (vibrator == null) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val clampedAmp = amplitude.coerceIn(1, 255)
                val effect = VibrationEffect.createOneShot(millis, clampedAmp)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(millis)
            }
        } catch (e: Throwable) {
            // Ignored
        }
    }
}

