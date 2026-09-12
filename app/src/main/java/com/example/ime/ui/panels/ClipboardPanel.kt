package com.example.ime.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ClipboardEntity
import com.example.ime.theme.KeyboardColorScheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClipboardPanel(
    modifier: Modifier = Modifier,
    clips: List<ClipboardEntity>,
    colorScheme: KeyboardColorScheme,
    onClipClick: (String) -> Unit,
    onTogglePin: (Long, Boolean) -> Unit,
    onDeleteClip: (Long) -> Unit,
    onClearUnpinned: () -> Unit,
    onClose: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFolder by remember { mutableStateOf("الكل") }
    val folders = listOf("الكل", "مهم", "عام", "أذكار", "روابط", "ملاحظات")

    val filteredClips = remember(clips, searchQuery, selectedFolder) {
        clips.filter { clip ->
            val matchesSearch = searchQuery.isEmpty() || clip.text.contains(searchQuery, ignoreCase = true)
            val matchesFolder = selectedFolder == "الكل" || clip.folder == selectedFolder
            matchesSearch && matchesFolder
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorScheme.background)
            .padding(8.dp)
    ) {
        // Header with Search and Clear
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "📋 الحافظة الدائمة",
                color = colorScheme.accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            // Search box
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
                        text = "🔍 بحث في الحافظة...",
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

            // Clear unpinned
            IconButton(
                onClick = onClearUnpinned,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "مسح غير المثبت",
                    tint = colorScheme.keyText.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Close button
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "إغلاق",
                    tint = colorScheme.keyText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Folder Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (folder in folders) {
                val isSelected = selectedFolder == folder
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) colorScheme.accent else colorScheme.keyBackground
                        )
                        .clickable { selectedFolder = folder }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = folder,
                        color = if (isSelected) Color.White else colorScheme.keyText,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // List of clips
        if (filteredClips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isNotEmpty()) "لا توجد نتائج مطابقة" else "الحافظة فارغة حالياً",
                    color = colorScheme.keyText.copy(alpha = 0.5f),
                    fontSize = 13.sp
                )
            }
        } else {
            val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredClips, key = { it.id }) { clip ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colorScheme.keyBackground)
                            .clickable { onClipClick(clip.text) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = clip.text,
                                color = colorScheme.keyText,
                                fontSize = 13.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = dateFormat.format(Date(clip.timestamp)),
                                    color = colorScheme.keyText.copy(alpha = 0.4f),
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "• ${clip.folder}",
                                    color = colorScheme.accent.copy(alpha = 0.8f),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Toggle Pin
                        IconButton(
                            onClick = { onTogglePin(clip.id, !clip.isPinned) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (clip.isPinned) Icons.Default.PushPin else Icons.Default.PushPin,
                                contentDescription = if (clip.isPinned) "إلغاء التثبيت" else "تثبيت",
                                tint = if (clip.isPinned) colorScheme.accent else colorScheme.keyText.copy(alpha = 0.35f),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Delete
                        IconButton(
                            onClick = { onDeleteClip(clip.id) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "حذف",
                                tint = colorScheme.keyText.copy(alpha = 0.35f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
