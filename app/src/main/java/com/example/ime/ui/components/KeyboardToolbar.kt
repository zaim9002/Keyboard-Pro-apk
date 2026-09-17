package com.example.ime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.util.HapticHelper

enum class KeyboardPanel {
    NONE,
    CLIPBOARD,
    EMOJI,
    STICKERS,
    GIFS,
    DECORATIONS,
    VOICE,
    TRANSLATE,
    DICTIONARY,
    EDITING,
    RESIZE,
    AI_ASSISTANT
}

@Composable
fun KeyboardToolbar(
    modifier: Modifier = Modifier,
    activePanel: KeyboardPanel,
    currentLanguage: String,
    isIncognito: Boolean,
    autoTranslateOnEnter: Boolean = false,
    oneHandedMode: String = "OFF",
    showTermuxKeys: Boolean = false,
    showQuickSnippets: Boolean = false,
    colorScheme: KeyboardColorScheme,
    onPanelSelect: (KeyboardPanel) -> Unit,
    onSwitchLanguage: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleOneHanded: ((String) -> Unit)? = null,
    onToggleTermuxKeys: (() -> Unit)? = null,
    onToggleQuickSnippets: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val view = LocalView.current

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(colorScheme.background),
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Settings Gear
        item(key = "settings") {
            ToolbarIconButton(
                icon = Icons.Default.Settings,
                tooltip = "إعدادات",
                isSelected = false,
                colorScheme = colorScheme,
                onClick = {
                    onOpenSettings()
                }
            )
        }

        // Language indicator button (ع / EN)
        val isArabic = currentLanguage == "ar"
        val langShort = if (isArabic) "ع" else currentLanguage.uppercase()
        item(key = "lang") {
            Box(
                modifier = Modifier
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.keyBackground.copy(alpha = 0.85f))
                    .clickable(role = androidx.compose.ui.semantics.Role.Button) {
                        HapticHelper.performKeyHaptic(context, view)
                        onSwitchLanguage()
                    }
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = langShort,
                    color = colorScheme.keyText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Termux / Developer Keys toggle (صف أزرار المطورين والترموكس)
        if (onToggleTermuxKeys != null) {
            item(key = "termux") {
                ToolbarIconButton(
                    icon = Icons.Default.Terminal,
                    tooltip = "صف مفاتيح المطورين والترموكس",
                    isSelected = showTermuxKeys,
                    badge = "DEV",
                    colorScheme = colorScheme,
                    onClick = onToggleTermuxKeys
                )
            }
        }

        // Quick Snippets / Emoji Bar toggle (شريط العبارات والإيموجي السريع)
        if (onToggleQuickSnippets != null) {
            item(key = "quick_snippets") {
                ToolbarIconButton(
                    icon = Icons.Default.FlashOn,
                    tooltip = "شريط الإيموجي والعبارات السريع",
                    isSelected = showQuickSnippets,
                    badge = "🔥",
                    colorScheme = colorScheme,
                    onClick = onToggleQuickSnippets
                )
            }
        }

        // Resize Keyboard button (خيار وزر تغيير حجم الكيبورد)
        item(key = "resize") {
            ToolbarIconButton(
                icon = Icons.Default.Height,
                tooltip = "تغيير حجم الكيبورد",
                isSelected = activePanel == KeyboardPanel.RESIZE,
                colorScheme = colorScheme,
                onClick = {
                    onPanelSelect(if (activePanel == KeyboardPanel.RESIZE) KeyboardPanel.NONE else KeyboardPanel.RESIZE)
                }
            )
        }

        // One-Handed Mode toggle button (وضع اليد الواحدة)
        if (onToggleOneHanded != null) {
            item(key = "one_handed") {
                ToolbarIconButton(
                    icon = Icons.Default.PanTool,
                    tooltip = "وضع اليد الواحدة",
                    isSelected = oneHandedMode != "OFF",
                    badge = if (oneHandedMode == "RIGHT") "يمين" else if (oneHandedMode == "LEFT") "يسار" else null,
                    colorScheme = colorScheme,
                    onClick = {
                        val nextMode = when (oneHandedMode) {
                            "OFF" -> "RIGHT"
                            "RIGHT" -> "LEFT"
                            else -> "OFF"
                        }
                        onToggleOneHanded(nextMode)
                    }
                )
            }
        }

        // AI Assistant (الذكاء الاصطناعي، تغيير نبرة الكتابة وتدقيق النصوص)
        item(key = "ai_assistant") {
            ToolbarIconButton(
                icon = Icons.Default.AutoAwesome,
                tooltip = "الذكاء الاصطناعي وتغيير النبرة",
                isSelected = activePanel == KeyboardPanel.AI_ASSISTANT,
                badge = "AI",
                colorScheme = colorScheme,
                onClick = {
                    onPanelSelect(if (activePanel == KeyboardPanel.AI_ASSISTANT) KeyboardPanel.NONE else KeyboardPanel.AI_ASSISTANT)
                }
            )
        }

        // Translate
        item(key = "translate") {
            ToolbarIconButton(
                icon = Icons.Default.Translate,
                tooltip = "ترجمة فورية",
                isSelected = activePanel == KeyboardPanel.TRANSLATE,
                badge = if (autoTranslateOnEnter) "⚡" else null,
                colorScheme = colorScheme,
                onClick = {
                    onPanelSelect(if (activePanel == KeyboardPanel.TRANSLATE) KeyboardPanel.NONE else KeyboardPanel.TRANSLATE)
                }
            )
        }

        // Clipboard
        item(key = "clipboard") {
            ToolbarIconButton(
                icon = Icons.Default.ContentPaste,
                tooltip = "الحافظة",
                isSelected = activePanel == KeyboardPanel.CLIPBOARD,
                colorScheme = colorScheme,
                onClick = {
                    onPanelSelect(if (activePanel == KeyboardPanel.CLIPBOARD) KeyboardPanel.NONE else KeyboardPanel.CLIPBOARD)
                }
            )
        }

        // Voice
        item(key = "voice") {
            ToolbarIconButton(
                icon = Icons.Default.Mic,
                tooltip = "كتابة بالصوت",
                isSelected = activePanel == KeyboardPanel.VOICE,
                colorScheme = colorScheme,
                onClick = {
                    onPanelSelect(if (activePanel == KeyboardPanel.VOICE) KeyboardPanel.NONE else KeyboardPanel.VOICE)
                }
            )
        }

        // Emoji
        item(key = "emoji") {
            ToolbarIconButton(
                icon = Icons.Default.Mood,
                tooltip = "إيموجي",
                isSelected = activePanel == KeyboardPanel.EMOJI,
                colorScheme = colorScheme,
                onClick = {
                    onPanelSelect(if (activePanel == KeyboardPanel.EMOJI) KeyboardPanel.NONE else KeyboardPanel.EMOJI)
                }
            )
        }

        // Decorations / AI
        item(key = "decorations") {
            ToolbarIconButton(
                icon = Icons.Default.AutoAwesome,
                tooltip = "زخرفة وذكاء اصطناعي",
                isSelected = activePanel == KeyboardPanel.DECORATIONS,
                colorScheme = colorScheme,
                onClick = {
                    onPanelSelect(if (activePanel == KeyboardPanel.DECORATIONS) KeyboardPanel.NONE else KeyboardPanel.DECORATIONS)
                }
            )
        }

        // Stickers
        item(key = "stickers") {
            ToolbarIconButton(
                icon = Icons.Default.Palette,
                tooltip = "ملصقات",
                isSelected = activePanel == KeyboardPanel.STICKERS,
                colorScheme = colorScheme,
                onClick = {
                    onPanelSelect(if (activePanel == KeyboardPanel.STICKERS) KeyboardPanel.NONE else KeyboardPanel.STICKERS)
                }
            )
        }

        // GIF
        item(key = "gifs") {
            ToolbarIconButton(
                icon = Icons.Default.Gif,
                tooltip = "GIF",
                isSelected = activePanel == KeyboardPanel.GIFS,
                colorScheme = colorScheme,
                onClick = {
                    onPanelSelect(if (activePanel == KeyboardPanel.GIFS) KeyboardPanel.NONE else KeyboardPanel.GIFS)
                }
            )
        }

        // Edit
        item(key = "editing") {
            ToolbarIconButton(
                icon = Icons.Default.Edit,
                tooltip = "تحديد ومؤشر",
                isSelected = activePanel == KeyboardPanel.EDITING,
                colorScheme = colorScheme,
                onClick = {
                    onPanelSelect(if (activePanel == KeyboardPanel.EDITING) KeyboardPanel.NONE else KeyboardPanel.EDITING)
                }
            )
        }

        // Incognito indicator
        if (isIncognito) {
            item(key = "incognito") {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(colorScheme.accent.copy(alpha = 0.25f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🕵️ خفي",
                        color = colorScheme.accent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolbarIconButton(
    icon: ImageVector,
    tooltip: String,
    isSelected: Boolean,
    badge: String? = null,
    colorScheme: KeyboardColorScheme,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val bg = if (isSelected) colorScheme.accent.copy(alpha = 0.35f) else colorScheme.keyBackground.copy(alpha = 0.7f)
    val tint = if (isSelected) colorScheme.accent else colorScheme.keyText

    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(bg)
            .clickable(
                role = androidx.compose.ui.semantics.Role.Button,
                onClick = {
                    HapticHelper.performKeyHaptic(context, view)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tooltip,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        if (badge != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
            ) {
                Text(text = badge, fontSize = 9.sp)
            }
        }
    }
}
