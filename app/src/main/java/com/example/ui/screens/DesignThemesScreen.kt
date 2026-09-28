package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.KeyboardProApp

data class DesignCardItem(
    val id: String,
    val title: String,
    val downloads: String,
    val isNew: Boolean = true,
    val headerSubtitle: String,
    val iconEmoji: String,
    val bgColor: Color,
    val cardBg: Color,
    val textColor: Color,
    val themePresetName: String
)

@Composable
fun DesignThemesScreen() {
    val context = LocalContext.current
    val prefs = remember { KeyboardProApp.instance.preferences }
    var activeTheme by remember { mutableStateOf(prefs.theme) }
    var selectedCategory by remember { mutableStateOf("Trends") }

    val categories = listOf("Trends", "Mukbang", "Cute 🍉😎", "Simple", "Animals", "Neon")

    val designCards = remember {
        listOf(
            DesignCardItem(
                id = "d1",
                title = "Pudding time",
                downloads = "41133",
                headerSubtitle = "Pudding\ntime",
                iconEmoji = "🍮 🍮 🍮",
                bgColor = Color(0xFFFEF3C7),
                cardBg = Color(0xFFFFFBEB),
                textColor = Color(0xFF92400E),
                themePresetName = "Pudding time"
            ),
            DesignCardItem(
                id = "d2",
                title = "Crunchy Apple Bite",
                downloads = "25933",
                headerSubtitle = "Crunchy\nApple Bite",
                iconEmoji = "🍎 🍎 🍎",
                bgColor = Color(0xFFFEE2E2),
                cardBg = Color(0xFFFEF2F2),
                textColor = Color(0xFF991B1B),
                themePresetName = "Crunchy Apple Bite"
            ),
            DesignCardItem(
                id = "d3",
                title = "Suitcase Full of Joy",
                downloads = "25259",
                headerSubtitle = "Suitcase\nFull of Joy",
                iconEmoji = "✈️ 🧳 🏖️",
                bgColor = Color(0xFFE0F2FE),
                cardBg = Color(0xFFF0F9FF),
                textColor = Color(0xFF075985),
                themePresetName = "Suitcase Full of Joy"
            ),
            DesignCardItem(
                id = "d4",
                title = "Chihuahuas never hold back",
                downloads = "42430",
                headerSubtitle = "Chihuahuas\nnever hold back.",
                iconEmoji = "🐶 🐾 🐶",
                bgColor = Color(0xFFF5EBE1),
                cardBg = Color(0xFFFDFBF7),
                textColor = Color(0xFF6B4730),
                themePresetName = "Chihuahuas never hold back"
            ),
            DesignCardItem(
                id = "d5",
                title = "Ice cream selection",
                downloads = "40692",
                headerSubtitle = "Ice cream\nselection",
                iconEmoji = "🍦 🍧 🍨",
                bgColor = Color(0xFFFCE7F3),
                cardBg = Color(0xFFFDF2F8),
                textColor = Color(0xFF9D174D),
                themePresetName = "Ice cream selection"
            ),
            DesignCardItem(
                id = "d6",
                title = "Summer forest fireflies",
                downloads = "45750",
                headerSubtitle = "Summer forest\nfireflies ✨",
                iconEmoji = "🌲 🌟 🌲",
                bgColor = Color(0xFF0F291E),
                cardBg = Color(0xFF061A12),
                textColor = Color(0xFFFEF08A),
                themePresetName = "Summer forest fireflies"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121214))
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Category Chips Bar (Screenshot 2)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "بحث",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )

            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Text(
                    text = cat,
                    color = if (isSelected) Color.White else Color.Gray,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier.clickable { selectedCategory = cat }
                )
            }
        }

        // Hero Banner Card: Crunchy Apple Bite (Screenshot 2 top banner)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFFEF4444), Color(0xFFF87171), Color(0xFFFEF3C7))
                    )
                )
                .clickable {
                    prefs.theme = "Crunchy Apple Bite"
                    activeTheme = "Crunchy Apple Bite"
                    Toast.makeText(context, "تم تطبيق ثيم: Crunchy Apple Bite", Toast.LENGTH_SHORT).show()
                }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = "Crunchy",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Apple Bite 🍎",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ثيم التصميم المميز والأكثر طلباً",
                        color = Color(0xFFFFF1F2),
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🍏", fontSize = 28.sp)
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text("تطبيق الثيم", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Grid of 6 Theme Cards (Screenshot 2)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DesignThemeCard(
                card = designCards[0],
                isSelected = activeTheme == designCards[0].themePresetName,
                modifier = Modifier.weight(1f),
                onClick = {
                    prefs.theme = designCards[0].themePresetName
                    activeTheme = designCards[0].themePresetName
                    Toast.makeText(context, "تم تطبيق: ${designCards[0].title}", Toast.LENGTH_SHORT).show()
                }
            )
            DesignThemeCard(
                card = designCards[1],
                isSelected = activeTheme == designCards[1].themePresetName,
                modifier = Modifier.weight(1f),
                onClick = {
                    prefs.theme = designCards[1].themePresetName
                    activeTheme = designCards[1].themePresetName
                    Toast.makeText(context, "تم تطبيق: ${designCards[1].title}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DesignThemeCard(
                card = designCards[2],
                isSelected = activeTheme == designCards[2].themePresetName,
                modifier = Modifier.weight(1f),
                onClick = {
                    prefs.theme = designCards[2].themePresetName
                    activeTheme = designCards[2].themePresetName
                    Toast.makeText(context, "تم تطبيق: ${designCards[2].title}", Toast.LENGTH_SHORT).show()
                }
            )
            DesignThemeCard(
                card = designCards[3],
                isSelected = activeTheme == designCards[3].themePresetName,
                modifier = Modifier.weight(1f),
                onClick = {
                    prefs.theme = designCards[3].themePresetName
                    activeTheme = designCards[3].themePresetName
                    Toast.makeText(context, "تم تطبيق: ${designCards[3].title}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DesignThemeCard(
                card = designCards[4],
                isSelected = activeTheme == designCards[4].themePresetName,
                modifier = Modifier.weight(1f),
                onClick = {
                    prefs.theme = designCards[4].themePresetName
                    activeTheme = designCards[4].themePresetName
                    Toast.makeText(context, "تم تطبيق: ${designCards[4].title}", Toast.LENGTH_SHORT).show()
                }
            )
            DesignThemeCard(
                card = designCards[5],
                isSelected = activeTheme == designCards[5].themePresetName,
                modifier = Modifier.weight(1f),
                onClick = {
                    prefs.theme = designCards[5].themePresetName
                    activeTheme = designCards[5].themePresetName
                    Toast.makeText(context, "تم تطبيق: ${designCards[5].title}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun DesignThemeCard(
    card: DesignCardItem,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E1E24))
            .border(
                width = if (isSelected) 2.dp else 0.8.dp,
                color = if (isSelected) Color(0xFF8B5CF6) else Color(0xFF33333D),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
    ) {
        Column {
            // Visual Card Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(98.dp)
                    .background(card.bgColor)
                    .padding(8.dp)
            ) {
                // NEW badge
                if (card.isNew) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF6366F1))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "NEW",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Center visual
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = card.headerSubtitle,
                        color = card.textColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = card.iconEmoji, fontSize = 14.sp)
                }
            }

            // Bottom Info Row: Title & Download Count (Screenshot 2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = card.downloads,
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = card.title,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}
