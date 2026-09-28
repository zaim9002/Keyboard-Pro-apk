package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.KeyboardProApp

data class ColorThemeBubbleItem(
    val name: String,
    val bubbleBg: Color,
    val keyPreviewBg: Color,
    val keySpecialPreviewBg: Color
)

@Composable
fun ColorThemesScreen() {
    val context = LocalContext.current
    val prefs = remember { KeyboardProApp.instance.preferences }
    var activeTheme by remember { mutableStateOf(prefs.theme) }

    // Exactly 24 themes as ordered in Screenshot 3
    val colorItems = remember {
        listOf(
            ColorThemeBubbleItem("أساسي", Color(0xFF1E2430), Color(0xFFFFFFFF), Color(0xFF818CF8)),
            ColorThemeBubbleItem("غزل بنات", Color(0xFFFCE7F3), Color(0xFFFFFFFF), Color(0xFFBAE6FD)),
            ColorThemeBubbleItem("وردي", Color(0xFFFFE4E6), Color(0xFFFFFFFF), Color(0xFFFDA4AF)),
            ColorThemeBubbleItem("مرجاني", Color(0xFFFFEDD5), Color(0xFFFFFFFF), Color(0xFFFDBA74)),

            ColorThemeBubbleItem("أحمر", Color(0xFF5E0D21), Color(0xFFFFFFFF), Color(0xFFFDA4AF)),
            ColorThemeBubbleItem("أحمر مانجو", Color(0xFF60210A), Color(0xFFFFFFFF), Color(0xFFFDBA74)),
            ColorThemeBubbleItem("قرنفلي أصفر", Color(0xFFFDE68A), Color(0xFFFFFFFF), Color(0xFFF59E0B)),
            ColorThemeBubbleItem("لافندر", Color(0xFFE9D5FF), Color(0xFFFFFFFF), Color(0xFFD8B4FE)),

            ColorThemeBubbleItem("أرجواني", Color(0xFF701A75), Color(0xFFFFFFFF), Color(0xFFF0ABFC)),
            ColorThemeBubbleItem("برغندي", Color(0xFF4A1224), Color(0xFFFFFFFF), Color(0xFFFB7185)),
            ColorThemeBubbleItem("عنبي", Color(0xFF341366), Color(0xFFFFFFFF), Color(0xFFC084FC)),
            ColorThemeBubbleItem("فانيليا", Color(0xFFFEF9C3), Color(0xFFFFFFFF), Color(0xFFFDE047)),

            ColorThemeBubbleItem("موز", Color(0xFF553F09), Color(0xFFFFFFFF), Color(0xFFFDE047)),
            ColorThemeBubbleItem("مستردة", Color(0xFF532F09), Color(0xFFFFFFFF), Color(0xFFFACC15)),
            ColorThemeBubbleItem("برتقالي", Color(0xFF5C230C), Color(0xFFFFFFFF), Color(0xFFFB923C)),
            ColorThemeBubbleItem("بني", Color(0xFF3D2318), Color(0xFFFFFFFF), Color(0xFFD97706)),

            ColorThemeBubbleItem("شوكولاتة نعناع", Color(0xFF25332F), Color(0xFF526E67), Color(0xFF2DD4BF)),
            ColorThemeBubbleItem("ليموني", Color(0xFF2B460D), Color(0xFFFFFFFF), Color(0xFFA3E635)),
            ColorThemeBubbleItem("زمردي", Color(0xFF0C5642), Color(0xFFFFFFFF), Color(0xFF6EE7B7)),
            ColorThemeBubbleItem("أخضر جرينلاند", Color(0xFF0A3B2E), Color(0xFFFFFFFF), Color(0xFF34D399)),

            ColorThemeBubbleItem("شاي أخضر", Color(0xFF223528), Color(0xFFFFFFFF), Color(0xFF86EFAC)),
            ColorThemeBubbleItem("أزرق داكن", Color(0xFF0E2250), Color(0xFF334B80), Color(0xFF60A5FA)),
            ColorThemeBubbleItem("الأزرق السلمي", Color(0xFF0B4267), Color(0xFFFFFFFF), Color(0xFF7DD3FC)),
            ColorThemeBubbleItem("نعناعي", Color(0xFF0B4644), Color(0xFFFFFFFF), Color(0xFF5EEAD4))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141416))
    ) {
        // Purple Top Header Bar matching Screenshot 3
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .background(Color(0xFF6366F1)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "سمة الألوان",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 4-Column Grid of Speech-Bubble Color Themes (Screenshot 3)
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(colorItems, key = { it.name }) { item ->
                val isSelected = activeTheme == item.name

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            prefs.theme = item.name
                            activeTheme = item.name
                            Toast.makeText(context, "تم تطبيق ثيم: ${item.name}", Toast.LENGTH_SHORT).show()
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Speech-Bubble Key Preview Container
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 6.dp))
                            .background(item.bubbleBg)
                            .border(
                                width = if (isSelected) 2.5.dp else 0.5.dp,
                                color = if (isSelected) Color(0xFF818CF8) else Color.Transparent,
                                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 6.dp)
                            )
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            // Checked mark for active theme
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "محدد",
                                tint = Color(0xFF818CF8),
                                modifier = Modifier.size(32.dp)
                            )
                        } else {
                            // Mini 8 keys arranged 4x2 inside the bubble
                            Column(
                                verticalArrangement = Arrangement.spacedBy(3.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    repeat(4) {
                                        Box(
                                            modifier = Modifier
                                                .size(width = 11.dp, height = 9.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(item.keyPreviewBg)
                                        )
                                    }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 11.dp, height = 9.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(item.keySpecialPreviewBg)
                                    )
                                    repeat(3) {
                                        Box(
                                            modifier = Modifier
                                                .size(width = 11.dp, height = 9.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(item.keyPreviewBg)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Theme Name Label
                    Text(
                        text = item.name,
                        color = if (isSelected) Color(0xFF818CF8) else Color(0xFFD1D5DB),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
