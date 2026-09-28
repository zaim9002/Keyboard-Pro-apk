package com.example.ime.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme

data class ToolbarItemConfig(
    val id: String,
    val title: String,
    val icon: ImageVector? = null,
    val textIcon: String? = null
)

@Composable
fun ToolbarEditorPanel(
    modifier: Modifier = Modifier,
    colorScheme: KeyboardColorScheme,
    onClose: () -> Unit
) {
    var activeItems by remember {
        mutableStateOf(
            listOf(
                ToolbarItemConfig("game", "لعبة", Icons.Default.SportsEsports),
                ToolbarItemConfig("voice", "صوت", Icons.Default.Mic),
                ToolbarItemConfig("translate", "ترجمة", Icons.Default.Translate),
                ToolbarItemConfig("quick_text", "نص سريع", Icons.Default.FlashOn),
                ToolbarItemConfig("theme", "سمة", Icons.Default.Checkroom),
                ToolbarItemConfig("emoji", "إيموجي", Icons.Default.Mood)
            )
        )
    }

    val allAvailableTools = listOf(
        ToolbarItemConfig("emoji", "إيموجي", Icons.Default.Mood),
        ToolbarItemConfig("handwriting", "كتابة بخط اليد", Icons.Default.Draw),
        ToolbarItemConfig("voice", "إدخال صوتي", Icons.Default.Mic),
        ToolbarItemConfig("text_edit", "تحرير النص", Icons.Default.OpenWith),
        ToolbarItemConfig("quick_text", "نص سريع", Icons.Default.FlashOn),
        ToolbarItemConfig("game", "لعبة صغيرة", Icons.Default.SportsEsports),
        ToolbarItemConfig("emoticons", "الرموز التعبيرية النصية :-)", textIcon = ":-)"),
        ToolbarItemConfig("translate", "ترجمة", Icons.Default.Translate),
        ToolbarItemConfig("notes", "ملاحظة", Icons.Default.EditNote),
        ToolbarItemConfig("numbers", "تشغيل/إيقاف الأرقام", textIcon = "123"),
        ToolbarItemConfig("news", "أخبار", Icons.Default.Newspaper),
        ToolbarItemConfig("theme", "السمة", Icons.Default.Checkroom),
        ToolbarItemConfig("font", "الخط", textIcon = "Aa"),
        ToolbarItemConfig("one_handed", "وضع اليد الواحدة", Icons.Default.Smartphone),
        ToolbarItemConfig("settings", "الإعدادات", Icons.Default.Settings),
        ToolbarItemConfig("insta_font", "خط Insta", textIcon = "𝓕"),
        ToolbarItemConfig("calc", "الحاسبة", Icons.Default.Calculate)
    )

    val socialShortcuts = listOf(
        ToolbarItemConfig("youtube", "موقع YouTube", Icons.Default.SmartDisplay),
        ToolbarItemConfig("instagram", "انستغرام", Icons.Default.CameraAlt),
        ToolbarItemConfig("twitter", "تويتر", Icons.Default.Chat),
        ToolbarItemConfig("facebook", "فيسبوك", Icons.Default.Public),
        ToolbarItemConfig("webtoon", "خط ويبتون", Icons.Default.Book),
        ToolbarItemConfig("whatsapp", "واتساب", Icons.Default.Call),
        ToolbarItemConfig("messenger", "الفيسبوك رسول", Icons.Default.Forum)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF161618))
            .padding(10.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.HelpOutline, contentDescription = "مساعدة", tint = Color.LightGray)
            }

            Text(
                text = "تحرير شريط الأدوات",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "رجوع", tint = Color.White)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Active items in toolbar with red minus badges
        Text(
            text = "الأدوات النشطة في الشريط (المس للحذف أو الترتيب):",
            color = Color.Gray,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF222226))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(activeItems) { item ->
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2E2E34)),
                    contentAlignment = Alignment.Center
                ) {
                    // Minus badge
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.TopStart)
                            .clip(CircleShape)
                            .background(Color(0xFFFF3B30))
                            .clickable {
                                if (activeItems.size > 2) {
                                    activeItems = activeItems.filter { it.id != item.id }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("-", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    if (item.icon != null) {
                        Icon(item.icon, contentDescription = item.title, tint = Color.White, modifier = Modifier.size(20.dp))
                    } else if (item.textIcon != null) {
                        Text(item.textIcon, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Scrollable available tools
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "جميع الأدوات المتاحة (المس للإضافة إلى الشريط):",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                // Available tools grid
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    allAvailableTools.chunked(3).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowItems.forEach { tool ->
                                val isAdded = activeItems.any { it.id == tool.id }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(64.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isAdded) Color(0xFF1E293B) else Color(0xFF242428))
                                        .clickable {
                                            if (!isAdded && activeItems.size < 7) {
                                                activeItems = activeItems + tool
                                            }
                                        }
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        if (tool.icon != null) {
                                            Icon(
                                                tool.icon,
                                                contentDescription = tool.title,
                                                tint = if (isAdded) Color(0xFF818CF8) else Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        } else if (tool.textIcon != null) {
                                            Text(
                                                tool.textIcon,
                                                color = if (isAdded) Color(0xFF818CF8) else Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            tool.title,
                                            color = if (isAdded) Color(0xFF818CF8) else Color.LightGray,
                                            fontSize = 9.sp,
                                            maxLines = 1,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            // Fill remaining space in row if chunk has less than 3
                            repeat(3 - rowItems.size) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            item {
                HorizontalDivider(color = Color.DarkGray, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    text = "اختصارات التطبيقات ومواقع التواصل:",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    socialShortcuts.chunked(3).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowItems.forEach { shortcut ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(58.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF242428))
                                        .clickable {
                                            if (!activeItems.any { it.id == shortcut.id } && activeItems.size < 7) {
                                                activeItems = activeItems + shortcut
                                            }
                                        }
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        if (shortcut.icon != null) {
                                            Icon(
                                                shortcut.icon,
                                                contentDescription = shortcut.title,
                                                tint = Color(0xFF818CF8),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            shortcut.title,
                                            color = Color.LightGray,
                                            fontSize = 9.sp,
                                            maxLines = 1,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            repeat(3 - rowItems.size) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        // Reset to initial state button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    activeItems = listOf(
                        ToolbarItemConfig("game", "لعبة", Icons.Default.SportsEsports),
                        ToolbarItemConfig("voice", "صوت", Icons.Default.Mic),
                        ToolbarItemConfig("translate", "ترجمة", Icons.Default.Translate),
                        ToolbarItemConfig("quick_text", "نص سريع", Icons.Default.FlashOn),
                        ToolbarItemConfig("theme", "سمة", Icons.Default.Checkroom),
                        ToolbarItemConfig("emoji", "إيموجي", Icons.Default.Mood)
                    )
                }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "التراجع عن الحالة الأولية",
                color = Color.LightGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Default.Refresh, contentDescription = "إعادة ضبط", tint = Color.LightGray, modifier = Modifier.size(16.dp))
        }
    }
}
