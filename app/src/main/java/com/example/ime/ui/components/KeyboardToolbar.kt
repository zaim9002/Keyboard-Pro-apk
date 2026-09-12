package com.example.ime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme

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
    EDITING
}

@Composable
fun KeyboardToolbar(
    modifier: Modifier = Modifier,
    activePanel: KeyboardPanel,
    currentLanguage: String,
    isIncognito: Boolean,
    colorScheme: KeyboardColorScheme,
    onPanelSelect: (KeyboardPanel) -> Unit,
    onSwitchLanguage: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(colorScheme.background)
            .horizontalScroll(scrollState)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Incognito indicator
        if (isIncognito) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(colorScheme.accent.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "🕵️ خفي",
                    color = colorScheme.accent,
                    fontSize = 11.sp
                )
            }
        }

        // Toolbar Action Items
        ToolbarIconButton(
            icon = Icons.Default.ContentPaste,
            tooltip = "الحافظة",
            isSelected = activePanel == KeyboardPanel.CLIPBOARD,
            colorScheme = colorScheme,
            onClick = {
                onPanelSelect(if (activePanel == KeyboardPanel.CLIPBOARD) KeyboardPanel.NONE else KeyboardPanel.CLIPBOARD)
            }
        )

        ToolbarIconButton(
            icon = Icons.Default.Mood,
            tooltip = "إيموجي",
            isSelected = activePanel == KeyboardPanel.EMOJI,
            colorScheme = colorScheme,
            onClick = {
                onPanelSelect(if (activePanel == KeyboardPanel.EMOJI) KeyboardPanel.NONE else KeyboardPanel.EMOJI)
            }
        )

        ToolbarIconButton(
            icon = Icons.Default.AutoAwesome,
            tooltip = "زخرفة",
            isSelected = activePanel == KeyboardPanel.DECORATIONS,
            colorScheme = colorScheme,
            onClick = {
                onPanelSelect(if (activePanel == KeyboardPanel.DECORATIONS) KeyboardPanel.NONE else KeyboardPanel.DECORATIONS)
            }
        )

        ToolbarIconButton(
            icon = Icons.Default.Palette,
            tooltip = "ملصقات",
            isSelected = activePanel == KeyboardPanel.STICKERS,
            colorScheme = colorScheme,
            onClick = {
                onPanelSelect(if (activePanel == KeyboardPanel.STICKERS) KeyboardPanel.NONE else KeyboardPanel.STICKERS)
            }
        )

        ToolbarIconButton(
            icon = Icons.Default.Gif,
            tooltip = "GIF",
            isSelected = activePanel == KeyboardPanel.GIFS,
            colorScheme = colorScheme,
            onClick = {
                onPanelSelect(if (activePanel == KeyboardPanel.GIFS) KeyboardPanel.NONE else KeyboardPanel.GIFS)
            }
        )

        ToolbarIconButton(
            icon = Icons.Default.Mic,
            tooltip = "كتابة بالصوت",
            isSelected = activePanel == KeyboardPanel.VOICE,
            colorScheme = colorScheme,
            onClick = {
                onPanelSelect(if (activePanel == KeyboardPanel.VOICE) KeyboardPanel.NONE else KeyboardPanel.VOICE)
            }
        )

        ToolbarIconButton(
            icon = Icons.Default.Translate,
            tooltip = "ترجمة",
            isSelected = activePanel == KeyboardPanel.TRANSLATE,
            colorScheme = colorScheme,
            onClick = {
                onPanelSelect(if (activePanel == KeyboardPanel.TRANSLATE) KeyboardPanel.NONE else KeyboardPanel.TRANSLATE)
            }
        )

        ToolbarIconButton(
            icon = Icons.Default.MenuBook,
            tooltip = "قاموس",
            isSelected = activePanel == KeyboardPanel.DICTIONARY,
            colorScheme = colorScheme,
            onClick = {
                onPanelSelect(if (activePanel == KeyboardPanel.DICTIONARY) KeyboardPanel.NONE else KeyboardPanel.DICTIONARY)
            }
        )

        ToolbarIconButton(
            icon = Icons.Default.Edit,
            tooltip = "تحديد ومؤشر",
            isSelected = activePanel == KeyboardPanel.EDITING,
            colorScheme = colorScheme,
            onClick = {
                onPanelSelect(if (activePanel == KeyboardPanel.EDITING) KeyboardPanel.NONE else KeyboardPanel.EDITING)
            }
        )

        // Language indicator button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(colorScheme.keyBackground)
                .clickable { onSwitchLanguage() }
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (currentLanguage == "ar") "🌐 عربي" else "🌐 EN",
                color = colorScheme.keyText,
                fontSize = 11.sp
            )
        }

        ToolbarIconButton(
            icon = Icons.Default.Settings,
            tooltip = "إعدادات",
            isSelected = false,
            colorScheme = colorScheme,
            onClick = onOpenSettings
        )
    }
}

@Composable
private fun ToolbarIconButton(
    icon: ImageVector,
    tooltip: String,
    isSelected: Boolean,
    colorScheme: KeyboardColorScheme,
    onClick: () -> Unit
) {
    val bg = if (isSelected) colorScheme.accent.copy(alpha = 0.25f) else colorScheme.keyBackground.copy(alpha = 0.6f)
    val tint = if (isSelected) colorScheme.accent else colorScheme.keyText.copy(alpha = 0.85f)

    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tooltip,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
    }
}
