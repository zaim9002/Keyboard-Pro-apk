package com.example.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.KeyboardProApp
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.theme.KeyboardThemes

@Composable
fun ThemesScreen() {
    val prefs = KeyboardProApp.instance.preferences
    var currentThemeName by remember { mutableStateOf(prefs.theme) }
    val currentTheme = KeyboardThemes.getTheme(currentThemeName)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Live Keyboard Theme Preview Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "معاينة حية: ${currentTheme.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "الثيم النشط حالياً ✓",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Simulated Mini-Keyboard View
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(currentTheme.background)
                        .padding(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Simulated Toolbar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🔍", fontSize = 12.sp)
                            Text("📋", fontSize = 12.sp)
                            Text("😊", fontSize = 12.sp)
                            Text("✨", fontSize = 12.sp)
                            Text("🎤", fontSize = 12.sp)
                            Spacer(modifier = Modifier.weight(1f))
                            Text("🌐 عربي", color = currentTheme.keyText, fontSize = 11.sp)
                        }

                        // Simulated Suggestion Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(currentTheme.suggestionBar)
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text("السلام عليكم", color = currentTheme.accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("مرحباً", color = currentTheme.suggestionText, fontSize = 11.sp)
                            Text("شكراً", color = currentTheme.suggestionText, fontSize = 11.sp)
                        }

                        // Simulated Key Rows
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            val sampleKeys = listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح")
                            for (k in sampleKeys) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(26.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(currentTheme.keyBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = k, color = currentTheme.keyText, fontSize = 11.sp)
                                }
                            }
                        }

                        // Simulated Bottom Row with Space and Accent Enter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(26.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(currentTheme.specialKeyBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("١٢٣", color = currentTheme.specialKeyText, fontSize = 10.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(3f)
                                    .height(26.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(currentTheme.keyBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("مسافة", color = currentTheme.keyText.copy(alpha = 0.5f), fontSize = 10.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(26.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(currentTheme.accent),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("↵", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = "اختر المظهر المفضل لديك:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // Themes Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(KeyboardThemes.allThemes) { theme ->
                ThemeCard(
                    theme = theme,
                    isSelected = theme.name == currentThemeName,
                    onSelect = {
                        prefs.theme = theme.name
                        currentThemeName = theme.name
                    }
                )
            }
        }
    }
}

@Composable
private fun ThemeCard(
    theme: KeyboardColorScheme,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .then(
                if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(14.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Theme color swatches
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.background)
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(theme.keyBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Text("A", color = theme.keyText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(theme.accent)
                )

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "محدد",
                        tint = theme.accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = theme.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (theme.isDark) "داكن" else "فاتح",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
