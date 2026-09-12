package com.example.ime.ui.components

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeyButton(
    modifier: Modifier = Modifier,
    text: String,
    secondaryText: String? = null,
    isSpecial: Boolean = false,
    fontSize: TextUnit = 19.sp,
    height: Dp = 48.dp,
    colorScheme: KeyboardColorScheme,
    hapticEnabled: Boolean = true,
    soundEnabled: Boolean = false,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val view = LocalView.current
    val bgColor = if (isSpecial) colorScheme.specialKeyBackground else colorScheme.keyBackground
    val textColor = if (isSpecial) colorScheme.specialKeyText else colorScheme.keyText

    Box(
        modifier = modifier
            .padding(horizontal = 2.dp, vertical = 2.5.dp)
            .height(height)
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = Color.Black.copy(alpha = 0.35f),
                spotColor = Color.Black.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .combinedClickable(
                onClick = {
                    if (hapticEnabled) {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    }
                    if (soundEnabled) {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                    }
                    onClick()
                },
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Main Key Text
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            fontWeight = if (isSpecial) FontWeight.Medium else FontWeight.Normal,
            textAlign = TextAlign.Center
        )

        // Secondary small text hint on top right corner
        if (!secondaryText.isNullOrEmpty()) {
            Text(
                text = secondaryText,
                color = textColor.copy(alpha = 0.5f),
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 1.5.dp, end = 3.dp)
            )
        }
    }
}

@Composable
fun RepeatingDeleteKeyButton(
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    colorScheme: KeyboardColorScheme,
    hapticEnabled: Boolean = true,
    soundEnabled: Boolean = false,
    onDelete: () -> Unit,
    onDeleteAll: (() -> Unit)? = null
) {
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    var isPressed by remember { mutableStateOf(false) }

    val currentOnDelete by rememberUpdatedState(onDelete)
    val currentOnDeleteAll by rememberUpdatedState(onDeleteAll)

    val bgColor = if (isPressed) colorScheme.specialKeyBackground.copy(alpha = 0.7f) else colorScheme.specialKeyBackground
    val iconColor = colorScheme.specialKeyText

    Box(
        modifier = modifier
            .padding(horizontal = 2.dp, vertical = 2.5.dp)
            .height(height)
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = Color.Black.copy(alpha = 0.35f),
                spotColor = Color.Black.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        if (hapticEnabled) {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        }
                        if (soundEnabled) {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                        }
                        // 1. Initial delete
                        currentOnDelete()

                        val repeatJob: Job = coroutineScope.launch {
                            delay(280L)
                            var repeatCount = 0
                            while (isActive) {
                                repeatCount++
                                if (repeatCount > 25 && currentOnDeleteAll != null) {
                                    currentOnDeleteAll?.invoke()
                                } else {
                                    currentOnDelete()
                                }
                                if (hapticEnabled && repeatCount % 3 == 0) {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                }
                                val delayTime = if (repeatCount > 18) 30L else if (repeatCount > 8) 50L else 75L
                                delay(delayTime)
                            }
                        }

                        tryAwaitRelease()
                        repeatJob.cancel()
                        isPressed = false
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Backspace,
            contentDescription = "حذف",
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

