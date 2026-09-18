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

import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned

private val KeyDefaultShape = RoundedCornerShape(6.dp)
private val KeyShadowColor = Color.Black.copy(alpha = 0.15f)

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
    hapticIntensity: String = "Medium",
    soundEnabled: Boolean = false,
    showPreview: Boolean = !isSpecial,
    onLongClickWithCoords: ((LayoutCoordinates) -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val view = LocalView.current
    val context = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }
    var keyCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnLongClick by rememberUpdatedState(onLongClick)
    val currentOnLongClickWithCoords by rememberUpdatedState(onLongClickWithCoords)

    val bgColor = if (isSpecial) {
        if (isPressed) colorScheme.specialKeyBackground.copy(alpha = 0.8f) else colorScheme.specialKeyBackground
    } else {
        if (isPressed) colorScheme.accent.copy(alpha = 0.4f) else colorScheme.keyBackground
    }
    val textColor = if (isSpecial) colorScheme.specialKeyText else colorScheme.keyText

    val hasLongClick = currentOnLongClickWithCoords != null || currentOnLongClick != null

    Box(
        modifier = modifier
            .padding(horizontal = 1.5.dp, vertical = 2.dp)
            .height(height)
            .onGloballyPositioned { keyCoordinates = it }
            .shadow(
                elevation = 1.dp,
                shape = KeyDefaultShape,
                ambientColor = KeyShadowColor,
                spotColor = KeyShadowColor
            )
            .clip(KeyDefaultShape)
            .background(bgColor)
            .pointerInput(text, hapticEnabled, soundEnabled, hapticIntensity, hasLongClick) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        if (hapticEnabled) {
                            HapticHelper.performKeyHaptic(context, view, hapticIntensity)
                        }
                        if (soundEnabled) {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                        }
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onLongPress = if (hasLongClick) {
                        {
                            if (hapticEnabled) {
                                HapticHelper.performKeyHaptic(context, view, hapticIntensity)
                            }
                            keyCoordinates?.let { coords ->
                                currentOnLongClickWithCoords?.invoke(coords)
                            } ?: currentOnLongClick?.invoke()
                        }
                    } else null,
                    onTap = {
                        currentOnClick()
                    }
                )
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

        // Key Press Preview Box (Consistent rectangular preview directly above the key)
        if (showPreview && isPressed && !isSpecial && text.isNotBlank() && text.length <= 2) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(0, -110),
                properties = PopupProperties(focusable = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 46.dp, height = 52.dp)
                        .shadow(6.dp, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorScheme.keyBackground)
                        .border(1.dp, colorScheme.accent.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        color = colorScheme.keyText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun RepeatingDeleteKeyButton(
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    colorScheme: KeyboardColorScheme,
    hapticEnabled: Boolean = true,
    hapticIntensity: String = "Medium",
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
                shape = KeyDefaultShape,
                ambientColor = KeyShadowColor,
                spotColor = KeyShadowColor
            )
            .clip(KeyDefaultShape)
            .background(bgColor)
            .pointerInput(hapticEnabled, soundEnabled, hapticIntensity) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var isWordDeleted = false
                    isPressed = true
                    if (hapticEnabled) {
                        HapticHelper.performKeyHaptic(context, view, hapticIntensity)
                    }
                    if (soundEnabled) {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                    }
                    // 1. Initial single delete
                    currentOnDelete()
                    val startTime = System.currentTimeMillis()

                    // Accelerated deletion loop: accelerates after 1.8s, and turbo accelerates after 4s!
                    val repeatJob: Job = coroutineScope.launch {
                        delay(320L) // initial hold delay before repeating
                        while (isActive) {
                            if (isWordDeleted) break
                            val elapsed = System.currentTimeMillis() - startTime
                            if (elapsed >= 4000L) {
                                // AFTER 4 SECONDS: SUPER FAST TURBO ACCELERATED DELETE!
                                currentOnDeleteWord?.invoke() ?: run {
                                    repeat(4) { currentOnDelete() }
                                }
                                if (hapticEnabled) {
                                    HapticHelper.performKeyHaptic(context, view, "Light")
                                }
                                delay(25L)
                            } else if (elapsed >= 1800L) {
                                // FAST DELETE (between 1.8s and 4s)
                                currentOnDelete()
                                if (hapticEnabled) {
                                    HapticHelper.performKeyHaptic(context, view, "Light")
                                }
                                delay(50L)
                            } else {
                                // NORMAL REPEAT (first 1.8s)
                                currentOnDelete()
                                if (hapticEnabled) {
                                    HapticHelper.performKeyHaptic(context, view, "Light")
                                }
                                delay(85L)
                            }
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
                                HapticHelper.performKeyHaptic(context, view, "Strong")
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

