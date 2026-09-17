package com.example.ime.ui.components

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.util.HapticHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

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
    showPreview: Boolean = !isSpecial,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val view = LocalView.current
    val context = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }

    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnLongClick by rememberUpdatedState(onLongClick)

    val bgColor = if (isSpecial) {
        if (isPressed) colorScheme.specialKeyBackground.copy(alpha = 0.8f) else colorScheme.specialKeyBackground
    } else {
        if (isPressed) colorScheme.accent.copy(alpha = 0.4f) else colorScheme.keyBackground
    }
    val textColor = if (isSpecial) colorScheme.specialKeyText else colorScheme.keyText

    Box(
        modifier = modifier
            .padding(horizontal = 1.5.dp, vertical = 2.dp)
            .height(height)
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(6.dp),
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .pointerInput(text, hapticEnabled, soundEnabled, currentOnLongClick != null) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    if (hapticEnabled) {
                        HapticHelper.performKeyHaptic(context, view)
                    }
                    if (soundEnabled) {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                    }

                    if (currentOnLongClick != null) {
                        var isLongClicked = false
                        try {
                            withTimeout(380L) {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                    if (!change.pressed) {
                                        break
                                    }
                                }
                            }
                            // Released before timeout -> Normal click
                            currentOnClick()
                        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                            // Timeout reached -> Long press triggered
                            isLongClicked = true
                            if (hapticEnabled) {
                                HapticHelper.performKeyHaptic(context, view)
                            }
                            currentOnLongClick?.invoke()
                            // Wait for release
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                if (!change.pressed) break
                            }
                        }
                    } else {
                        // Fast instant tap path
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) {
                                currentOnClick()
                                break
                            }
                        }
                    }
                    isPressed = false
                }
            },
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
                color = textColor.copy(alpha = 0.55f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
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
    onDeleteWord: (() -> Unit)? = null,
    onDeleteAll: (() -> Unit)? = null
) {
    val view = LocalView.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isPressed by remember { mutableStateOf(false) }

    val currentOnDelete by rememberUpdatedState(onDelete)
    val currentOnDeleteWord by rememberUpdatedState(onDeleteWord)
    val currentOnDeleteAll by rememberUpdatedState(onDeleteAll)

    val bgColor = if (isPressed) colorScheme.specialKeyBackground.copy(alpha = 0.7f) else colorScheme.specialKeyBackground
    val iconColor = colorScheme.specialKeyText

    Box(
        modifier = modifier
            .padding(horizontal = 1.5.dp, vertical = 2.dp)
            .height(height)
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(6.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var isWordDeleted = false
                    isPressed = true
                    if (hapticEnabled) {
                        HapticHelper.performKeyHaptic(context, view)
                    }
                    if (soundEnabled) {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                    }
                    // 1. Initial delete
                    currentOnDelete()

                    // Steady, controlled repetition (smooth and comfortable, not too fast)
                    val repeatJob: Job = coroutineScope.launch {
                        delay(450L) // Comfortable initial delay before repeat
                        var repeatCount = 0
                        while (isActive) {
                            if (isWordDeleted) break
                            repeatCount++
                            currentOnDelete()
                            if (hapticEnabled && repeatCount % 2 == 0) {
                                HapticHelper.performKeyHaptic(context, view)
                            }
                            delay(125L) // Calm, controlled deletion speed
                        }
                    }

                    // Listen for release or left-swipe to delete full word
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) {
                            break
                        }
                        val dragX = change.position.x - down.position.x
                        // Swipe to the left by 40+ pixels: delete whole word!
                        if (dragX < -40f && !isWordDeleted) {
                            isWordDeleted = true
                            repeatJob.cancel()
                            if (hapticEnabled) {
                                HapticHelper.performKeyHaptic(context, view)
                            }
                            currentOnDeleteWord?.invoke()
                        }
                    }

                    repeatJob.cancel()
                    isPressed = false
                }
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

