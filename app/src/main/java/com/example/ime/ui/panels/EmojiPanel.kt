package com.example.ime.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.KeyboardProApp
import com.example.engine.EmojiData
import kotlinx.coroutines.launch
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.util.HapticHelper

private const val CATEGORY_RECENT = "recent"

@Composable
fun EmojiPanel(
    modifier: Modifier = Modifier,
    colorScheme: KeyboardColorScheme,
    onEmojiClick: (String) -> Unit,
    onBackspace: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val prefs = remember { KeyboardProApp.instance.preferences }

    var recentEmojis by remember { mutableStateOf(prefs.getRecentEmojis()) }
    // If recent emojis exist, default to "recent", otherwise first category
    var selectedCategoryId by remember {
        mutableStateOf(if (recentEmojis.isNotEmpty()) CATEGORY_RECENT else EmojiData.categories.first().id)
    }
    var searchQuery by remember { mutableStateOf("") }

    val currentEmojis = remember(selectedCategoryId, searchQuery, recentEmojis) {
        if (searchQuery.isNotEmpty()) {
            EmojiData.search(searchQuery)
        } else if (selectedCategoryId == CATEGORY_RECENT) {
            if (recentEmojis.isNotEmpty()) recentEmojis else listOf("😀", "❤️", "🔥", "👍", "✨", "😂", "🌹", "🙏")
        } else {
            EmojiData.categories.find { it.id == selectedCategoryId }?.emojis ?: emptyList()
        }
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(top = 4.dp, bottom = 4.dp, start = 6.dp, end = 6.dp)
    ) {
        // Top row: Search Bar & Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Search / Title Pill with Emoji Style badge
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.keyBackground)
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (selectedCategoryId == CATEGORY_RECENT) "🕒 أحدث الفيسات المستخدمة" else "✨ الرموز التعبيرية والفيسات",
                        color = colorScheme.keyText.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = prefs.emojiStyle,
                        color = colorScheme.accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Backspace Button
            IconButton(
                onClick = {
                    HapticHelper.performKeyHaptic(context, view)
                    onBackspace()
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "حذف",
                    tint = colorScheme.keyText,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Close Button
            IconButton(
                onClick = {
                    HapticHelper.performKeyHaptic(context, view)
                    onClose()
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "رجوع",
                    tint = colorScheme.accent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Category icons tab row (including Recently Used icon at the start)
        if (searchQuery.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Recently Used Emojis Tab
                val isRecentSelected = selectedCategoryId == CATEGORY_RECENT
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isRecentSelected) colorScheme.accent.copy(alpha = 0.25f) else colorScheme.keyBackground.copy(alpha = 0.4f)
                        )
                        .clickable {
                            HapticHelper.performKeyHaptic(context, view)
                            selectedCategoryId = CATEGORY_RECENT
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🕒",
                        fontSize = 16.sp
                    )
                }

                // 2. Standard Emoji Categories
                for (category in EmojiData.categories) {
                    val isSelected = selectedCategoryId == category.id
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) colorScheme.accent.copy(alpha = 0.25f) else colorScheme.keyBackground.copy(alpha = 0.4f)
                            )
                            .clickable {
                                HapticHelper.performKeyHaptic(context, view)
                                selectedCategoryId = category.id
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category.icon,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        // Emoji Grid with high-performance indexed items
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 40.dp),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                count = currentEmojis.size,
                key = { index -> "${selectedCategoryId}_${index}_${currentEmojis[index]}" }
            ) { index ->
                val emoji = currentEmojis[index]
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            HapticHelper.performKeyHaptic(context, view)
                            onEmojiClick(emoji)
                            recentEmojis = (listOf(emoji) + recentEmojis.filter { it != emoji }).take(30)
                            coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                prefs.addRecentEmoji(emoji)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
