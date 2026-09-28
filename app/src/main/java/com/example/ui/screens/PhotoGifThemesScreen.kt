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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.KeyboardProApp

data class WallpaperThemeItem(
    val id: String,
    val title: String,
    val imageUrl: String,
    val isGif: Boolean = false,
    val themePreset: String = "Midnight"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGifThemesScreen() {
    val context = LocalContext.current
    val prefs = remember { KeyboardProApp.instance.preferences }
    var activeTheme by remember { mutableStateOf(prefs.theme) }
    var searchQuery by remember { mutableStateOf("") }

    val photoThemes = remember {
        listOf(
            WallpaperThemeItem("p1", "المرعى الأخضر", "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?w=500&q=80", false, "أخضر جرينلاند"),
            WallpaperThemeItem("p2", "قمم الجبال الثلجية", "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=500&q=80", false, "الأزرق السلمي"),
            WallpaperThemeItem("p3", "أوراق الشجر الخضراء", "https://images.unsplash.com/photo-1513836279014-a89f7a76ae86?w=500&q=80", false, "زمردي"),
            WallpaperThemeItem("p4", "التل البديع", "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=500&q=80", false, "شاي أخضر"),
            WallpaperThemeItem("p5", "برج وسط الطبيعة", "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=500&q=80", false, "بني"),
            WallpaperThemeItem("p6", "سماء الصيف الصافية", "https://images.unsplash.com/photo-1534088568595-a066f410bcda?w=500&q=80", false, "أزرق داكن")
        )
    }

    val gifThemes = remember {
        listOf(
            WallpaperThemeItem("g1", "أغنام في المروج", "https://media.giphy.com/media/bbshzgyFQDqPHXBo4c/giphy.gif", true, "أخضر جرينلاند"),
            WallpaperThemeItem("g2", "قارب في البحر الأزرق", "https://media.giphy.com/media/26ufdipQqU2lhNA4g/giphy.gif", true, "الأزرق السلمي"),
            WallpaperThemeItem("g3", "حديقة خضراء وجسر", "https://media.giphy.com/media/3o7TKoWXm3okO1kgHC/giphy.gif", true, "زمردي"),
            WallpaperThemeItem("g4", "بحيرة هادئة بديعة", "https://media.giphy.com/media/3oriO04qxVReM5rJEA/giphy.gif", true, "شاي أخضر"),
            WallpaperThemeItem("g5", "حفيف أوراق الشجر", "https://media.giphy.com/media/l41JRsph73VokN6ik/giphy.gif", true, "ليموني"),
            WallpaperThemeItem("g6", "نخيل يتراقص مع النسيم", "https://media.giphy.com/media/3o6Zt481isNVuQI1l6/giphy.gif", true, "مرجاني")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121214))
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Bar (Screenshot 1 top)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF26262B))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "ابحث عن مزيد من الخلفيات!",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }
        }

        // Keywords Chips Row (Screenshot 1)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF2C2248))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("كلمة رئيسية شائعة", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text("3", color = Color(0xFFA855F7), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("الخريف", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF8B5CF6))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text("NEW", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            listOf("طبيعة ساحرة", "سماء ونجوم", "شتاء وثلج", "أنمي لطيف").forEach { tag ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF26262B))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(tag, color = Color.LightGray, fontSize = 12.sp)
                }
            }
        }

        // Section 1: سمات الصور الفوتوغرافية الموصى بها (Screenshot 1)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سمات الصور الفوتوغرافية الموصى بها",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "المزيد",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        Toast.makeText(context, "جاري استعراض المزيد من الخلفيات", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // 6-grid photos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                photoThemes.take(3).forEach { item ->
                    WallpaperCard(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            prefs.theme = item.themePreset
                            activeTheme = item.themePreset
                            Toast.makeText(context, "تم تطبيق ثيم: ${item.title}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                photoThemes.drop(3).take(3).forEach { item ->
                    WallpaperCard(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            prefs.theme = item.themePreset
                            activeTheme = item.themePreset
                            Toast.makeText(context, "تم تطبيق ثيم: ${item.title}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Section 2: سمات GIF الموصى بها (Screenshot 1)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سمات GIF الموصى بها",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "المزيد",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        Toast.makeText(context, "جاري استعراض المزيد من خلفيات GIF", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // 6-grid GIF themes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                gifThemes.take(3).forEach { item ->
                    WallpaperCard(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            prefs.theme = item.themePreset
                            activeTheme = item.themePreset
                            Toast.makeText(context, "تم تطبيق ثيم GIF: ${item.title}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                gifThemes.drop(3).take(3).forEach { item ->
                    WallpaperCard(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            prefs.theme = item.themePreset
                            activeTheme = item.themePreset
                            Toast.makeText(context, "تم تطبيق ثيم GIF: ${item.title}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Section 3: معرض صوري (Screenshot 1)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "معرض صوري",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // الكاميرا card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(86.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF6B46C1), Color(0xFF9F7AEA))
                            )
                        )
                        .clickable {
                            Toast.makeText(context, "فتح الكاميرا لالتقاط خلفية مخصصة", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "الكاميرا",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "الكاميرا",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // معرض الصور card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(86.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF553C9A), Color(0xFFB794F4))
                            )
                        )
                        .clickable {
                            Toast.makeText(context, "اختيار صورة أو GIF من ألبوم الصور", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Collections,
                            contentDescription = "معرض الصور",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "معرض الصور",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WallpaperCard(
    item: WallpaperThemeItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(82.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF26262B))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (item.isGif) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "GIF",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
