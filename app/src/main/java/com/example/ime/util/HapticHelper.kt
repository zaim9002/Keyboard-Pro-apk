package com.example.ime.util

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View

object HapticHelper {

    fun performKeyHaptic(context: Context?, view: View?) {
        try {
            // 1. Try View haptic feedback ignoring global disable if possible
            val performed = view?.performHapticFeedback(
                HapticFeedbackConstants.KEYBOARD_TAP,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            ) ?: false

            if (!performed && context != null) {
                // 2. Direct Vibrator invocation for guaranteed crisp haptic feedback
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    val vibrator = vibratorManager?.defaultVibrator
                    if (vibrator?.hasVibrator() == true) {
                        val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                        vibrator.vibrate(effect)
                        return
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    if (vibrator?.hasVibrator() == true) {
                        val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                        vibrator.vibrate(effect)
                        return
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    if (vibrator?.hasVibrator() == true) {
                        val effect = VibrationEffect.createOneShot(20L, VibrationEffect.DEFAULT_AMPLITUDE)
                        vibrator.vibrate(effect)
                        return
                    }
                }

                @Suppress("DEPRECATION")
                val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(20L)
            }
        } catch (e: Throwable) {
            // Safety catch for any device-specific vibration restriction
        }
    }
}
