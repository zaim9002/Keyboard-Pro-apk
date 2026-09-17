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
                isVibratorInitialized = true
            } catch (e: Throwable) {
                isVibratorInitialized = true
            }
        }
        return cachedVibrator
    }

    fun performKeyHaptic(context: Context?, view: View?) {
        try {
            // 1. Fast View haptic feedback
            val performed = view?.performHapticFeedback(
                HapticFeedbackConstants.KEYBOARD_TAP,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            ) ?: false

            if (!performed) {
                val vibrator = getVibrator(context) ?: return
                if (vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                        vibrator.vibrate(effect)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val effect = VibrationEffect.createOneShot(12L, VibrationEffect.DEFAULT_AMPLITUDE)
                        vibrator.vibrate(effect)
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(12L)
                    }
                }
            }
        } catch (e: Throwable) {
            // Ignored for safety
        }
    }
}

