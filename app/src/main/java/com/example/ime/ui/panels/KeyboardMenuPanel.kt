package com.example.ime.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

data class MenuItemData(
    val id: String,
    val title: String,
    val icon: ImageVector? = null,
    val textIcon: String? = null,
    val hasBadge: Boolean = false
)

@Composable
fun KeyboardMenuPanel(
    modifier: Modifier = Modifier,
    colorScheme: KeyboardColorScheme,
    onOpenThemes: () -> Unit,
    onOpenVoice: () -> Unit,
    onOpenMiniGame: () -> Unit,
    onOpenTranslate: () -> Unit,
    onOpenQuickText: () -> Unit,
    onOpenTextEditing: () -> Unit,
    onOpenCalculator: () -> Unit,
    onOpenNotes: () -> Unit,
    onToggleOneHanded: () -> Unit,
    onOpenNews: () -> Unit,
    onOpenFonts: () -> Unit,
    onToggleNumberRow: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenInstaFonts: () -> Unit,
    onOpenHandwriting: () -> Unit,
    onOpenToolbarEditor: () -> Unit,
    onClose: () -> Unit
) {
    val menuItems = listOf(
        MenuItemData("theme", "السمة", icon = Icons.Default.Checkroom, hasBadge = true),
        MenuItemData("voice", "إدخال صوتي", icon = Icons.Default.Mic),
        MenuItemData("game", "لعبة صغيرة", icon = Icons.Default.SportsEsports, hasBadge = true),
        MenuItemData("translate", "ترجمة", icon = Icons.Default.Translate),
        MenuItemData("quick_text", "نص سريع", icon = Icons.Default.FlashOn),
        MenuItemData("text_edit", "تحرير النص", icon = Icons.Default.OpenWith),
        MenuItemData("calc", "الحاسبة", icon = Icons.Default.Calculate),
        MenuItemData("notes", "ملاحظة", icon = Icons.Default.EditNote),
        MenuItemData("one_handed", "وضع اليد الواحدة", icon = Icons.Default.Smartphone),
        MenuItemData("news", "أخبار", icon = Icons.Default.Newspaper),
        MenuItemData("font", "الخط", textIcon = "Aa"),
        MenuItemData("numbers", "تشغيل/إيقاف الأرقام", textIcon = "123"),
        MenuItemData("settings", "الإعدادات", icon = Icons.Default.Settings),
        MenuItemData("insta_font", "خط Insta", textIcon = "𝓕"),
        MenuItemData("handwriting", "كتابة بخط اليد", icon = Icons.Default.Draw)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onOpenToolbarEditor,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "تحرير شريط الأدوات",
                    tint = colorScheme.keyText,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "قائمة لوحة المفاتيح",
                color = colorScheme.keyText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.Keyboard,
                    contentDescription = "الرجوع للوحة المفاتيح",
                    tint = colorScheme.keyText,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // 4 Columns Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(bottom = 6.dp)
        ) {
            items(menuItems) { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colorScheme.keyBackground)
                        .clickable {
                            when (item.id) {
                                "theme" -> onOpenThemes()
                                "voice" -> onOpenVoice()
                                "game" -> onOpenMiniGame()
                                "translate" -> onOpenTranslate()
                                "quick_text" -> onOpenQuickText()
                                "text_edit" -> onOpenTextEditing()
                                "calc" -> onOpenCalculator()
                                "notes" -> onOpenNotes()
                                "one_handed" -> onToggleOneHanded()
                                "news" -> onOpenNews()
                                "font" -> onOpenFonts()
                                "numbers" -> onToggleNumberRow()
                                "settings" -> onOpenSettings()
                                "insta_font" -> onOpenInstaFonts()
                                "handwriting" -> onOpenHandwriting()
                            }
                        }
                        .padding(4.dp)
                ) {
                    // Red Notification Badge
                    if (item.hasBadge) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF3B30))
                                .align(Alignment.TopStart)
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (item.icon != null) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = colorScheme.keyText,
                                modifier = Modifier.size(22.dp)
                            )
                        } else if (item.textIcon != null) {
                            Text(
                                text = item.textIcon,
                                color = colorScheme.keyText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = item.title,
                            color = colorScheme.keyText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
