package com.example.ime.ui.components

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
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
        if (isPressed) colorScheme.keyBackground.copy(alpha = 0.85f) else colorScheme.keyBackground
    }
    val textColor = if (isSpecial) colorScheme.specialKeyText else colorScheme.keyText

    Box(
        modifier = modifier
            .padding(horizontal = 1.5.dp, vertical = 2.dp)
            .height(height)
            .shadow(
                elevation = if (isPressed) 0.5.dp else 1.dp,
                shape = RoundedCornerShape(6.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .pointerInput(text, hapticEnabled, soundEnabled, isSpecial) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        if (hapticEnabled) {
                            HapticHelper.performKeyHaptic(context, view)
                        }
                        if (soundEnabled) {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                        }
                        val released = tryAwaitRelease()
                        isPressed = false
                        if (released) {
                            currentOnClick()
                        }
                    },
                    onLongPress = {
                        isPressed = false
                        currentOnLongClick?.invoke()
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
                color = textColor.copy(alpha = 0.5f),
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 1.5.dp, end = 3.dp)
            )
        }

        // Floating Key Preview Popup (like Gboard / iOS keyboard)
        if (isPressed && showPreview && text.isNotBlank()) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(x = 0, y = -140),
                properties = PopupProperties(
                    focusable = false,
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                Box(
                    modifier = Modifier
                        .width(58.dp)
                        .height(66.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(12.dp),
                            ambientColor = Color.Black.copy(alpha = 0.45f),
                            spotColor = Color.Black.copy(alpha = 0.5f)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorScheme.keyBackground)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (!secondaryText.isNullOrEmpty()) {
                            Text(
                                text = secondaryText,
                                color = textColor.copy(alpha = 0.55f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = text,
                            color = textColor,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
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
    soundEnabled: Boolean = false,
    onDelete: () -> Unit,
    onDeleteAll: (() -> Unit)? = null
) {
    val view = LocalView.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isPressed by remember { mutableStateOf(false) }

    val currentOnDelete by rememberUpdatedState(onDelete)
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
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        if (hapticEnabled) {
                            HapticHelper.performKeyHaptic(context, view)
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
                                    HapticHelper.performKeyHaptic(context, view)
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

