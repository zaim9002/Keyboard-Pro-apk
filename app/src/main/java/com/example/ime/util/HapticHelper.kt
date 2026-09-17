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

    fun performKeyHaptic(context: Context?, view: View?) {
        try {
            // 1. Fast View haptic feedback (0ms latency direct hardware signal)
            val performed = view?.performHapticFeedback(
                HapticFeedbackConstants.KEYBOARD_TAP,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            ) ?: false

            if (!performed && hasVibratorHardware) {
                val vibrator = getVibrator(context) ?: return
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                    vibrator.vibrate(effect)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createOneShot(10L, VibrationEffect.DEFAULT_AMPLITUDE)
                    vibrator.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(10L)
                }
            }
        } catch (e: Throwable) {
            // Ignored for safety
        }
    }
}

