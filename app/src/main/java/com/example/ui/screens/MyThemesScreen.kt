package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ime.theme.KeyboardThemes

@Composable
fun MyThemesScreen() {
    val context = LocalContext.current
    val prefs = remember { KeyboardProApp.instance.preferences }
    var currentThemeName by remember { mutableStateOf(prefs.theme) }

    val currentTheme = remember(currentThemeName) {
        KeyboardThemes.getTheme(currentThemeName, prefs = prefs)
    }

    val mySavedThemes = remember {
        listOf("أساسي", "Crunchy Apple Bite", "الأزرق السلمي", "وردي", "غزل بنات", "Summer forest fireflies")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121214))
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: السمة النشطة الحالية (Active Theme Card with Live Mini Keyboard Preview)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Text(
                            text = "السمة النشطة: ${currentTheme.name}",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF6366F1).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("مفعّل ✓", color = Color(0xFF818CF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Mini Keyboard Live Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(currentTheme.background)
                        .border(1.dp, currentTheme.borderColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Toolbar preview
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🎬 GIF", color = currentTheme.accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("📋 الحافظة", color = currentTheme.keyText, fontSize = 10.sp)
                            Text("🎙️ صوت", color = currentTheme.keyText, fontSize = 10.sp)
                            Text("⚙️", color = currentTheme.keyText, fontSize = 10.sp)
                        }

                        // Row 1
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح").forEach { char ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(28.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(currentTheme.keyBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(char, color = currentTheme.keyText, fontSize = 11.sp)
                                }
                            }
                        }

                        // Row 2
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            listOf("ش", "س", "ي", "ب", "ل", "ا", "ت", "ن", "م", "ك").forEach { char ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(28.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(currentTheme.keyBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(char, color = currentTheme.keyText, fontSize = 11.sp)
                                }
                            }
                        }

                        // Space row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(28.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(currentTheme.specialKeyBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("!؟1", color = currentTheme.specialKeyText, fontSize = 10.sp)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(4f)
                                    .height(28.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(currentTheme.keyBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("العربية", color = currentTheme.keyText.copy(alpha = 0.6f), fontSize = 10.sp)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(28.dp)
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

        // Section: سماتي المفضلة (Saved themes quick switch)
        Text(
            text = "سماتي المفضلة",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(mySavedThemes) { themeName ->
                val theme = KeyboardThemes.getTheme(themeName)
                val isSelected = currentThemeName == themeName

                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E1E24))
                        .border(
                            if (isSelected) 2.dp else 0.5.dp,
                            if (isSelected) Color(0xFF818CF8) else Color(0xFF33333D),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            prefs.theme = themeName
                            currentThemeName = themeName
                            Toast.makeText(context, "تم التبديل إلى: $themeName", Toast.LENGTH_SHORT).show()
                        }
                        .padding(10.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(theme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                repeat(3) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(theme.keyBackground)
                                    )
                                }
                            }
                        }

                        Text(
                            text = themeName,
                            color = if (isSelected) Color(0xFF818CF8) else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Section: تخصيص وتصميم سمة جديدة (Custom Studio actions)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "صانع الثيمات المخصص",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "يمكنك إنشاء سمات مخصصة، واختيار ألوان الخلفية، المفاتيح، والخطوط بحرية تامة.",
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            prefs.theme = "أساسي"
                            currentThemeName = "أساسي"
                            Toast.makeText(context, "تمت استعادة الثيم الأساسي", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF33333D)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("استعادة الافتراضي", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "تم حفظ إعدادات السمة بنجاح", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("حفظ السمة", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
