package com.example.ime.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.EmojiData
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.util.HapticHelper

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
    var selectedCategoryId by remember { mutableStateOf(EmojiData.categories.first().id) }
    var searchQuery by remember { mutableStateOf("") }

    val currentEmojis = remember(selectedCategoryId, searchQuery) {
        if (searchQuery.isNotEmpty()) {
            EmojiData.search(searchQuery)
        } else {
            EmojiData.categories.find { it.id == selectedCategoryId }?.emojis ?: emptyList()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(top = 4.dp, bottom = 4.dp, start = 6.dp, end = 6.dp)
    ) {
        // Top row: Search Bar & Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.keyBackground)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "🔍 بحث عن إيموجي (حب، ضحك، قلب...)",
                        color = colorScheme.keyText.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = colorScheme.keyText,
                        fontSize = 12.sp
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Quick Backspace in emoji panel
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

            // Back to Keyboard button
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

        // Category icons tab row
        if (searchQuery.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
