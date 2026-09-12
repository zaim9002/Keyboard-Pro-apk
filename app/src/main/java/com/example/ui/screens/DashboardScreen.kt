package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.KeyboardProApp

@Composable
fun DashboardScreen(
    onNavigateToThemes: () -> Unit,
    onNavigateToClipboard: () -> Unit,
    onNavigateToShortcuts: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLanguages: () -> Unit
) {
    val context = LocalContext.current
    val prefs = KeyboardProApp.instance.preferences
    val imm = remember { context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager }

    var testInputText by remember { mutableStateOf("") }
    var wordsCount by remember { mutableStateOf(prefs.wordsTypedCount) }

    // Helper functions to check IME status
    fun isImeEnabled(): Boolean {
        return try {
            val list = imm?.enabledInputMethodList
            if (list != null && list.any { it.packageName == context.packageName }) {
                true
            } else {
                val enabledImes = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ENABLED_INPUT_METHODS
                ) ?: ""
                enabledImes.contains(context.packageName)
            }
        } catch (e: Exception) {
            false
        }
    }

    fun isImeDefault(): Boolean {
        return try {
            val defaultIme = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.DEFAULT_INPUT_METHOD
            ) ?: ""
            defaultIme.contains(context.packageName)
        } catch (e: Exception) {
            false
        }
    }

    var isEnabled by remember { mutableStateOf(isImeEnabled()) }
    var isDefault by remember { mutableStateOf(isImeDefault()) }

    // Refresh state when composable is active
    LaunchedEffect(Unit) {
        isEnabled = isImeEnabled()
        isDefault = isImeDefault()
        wordsCount = prefs.wordsTypedCount
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF0284C7))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⌨️", fontSize = 24.sp)
                    }
                    Column {
                        Text(
                            text = "كيبورد محمد v3",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "كيبورد احترافي متكامل لنظام Android",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FeatureBadge("🛡️ خصوصية 100%")
                    FeatureBadge("⚡ سرعة فائقة")
                    FeatureBadge("✨ زخرفة فورية")
                }
            }
        }

        // Activation & Setup Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "حالة تفعيل لوحة المفاتيح",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Step 1: Enable in Settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isEnabled) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (isEnabled) Color(0xFF10B981) else Color(0xFFF59E0B)
                        )
                        Column {
                            Text(
                                text = "1. التفعيل في إعدادات النظام",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (isEnabled) "مفعّل وجاهز" else "غير مفعّل بعد",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = {
                            context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEnabled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isEnabled) "تعديل" else "تفعيل الآن",
                            fontSize = 11.sp,
                            color = if (isEnabled) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Step 2: Set as Default
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isDefault) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (isDefault) Color(0xFF10B981) else Color(0xFFF59E0B)
                        )
                        Column {
                            Text(
                                text = "2. التعيين ككيبورد افتراضي",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (isDefault) "لوحة المفاتيح الافتراضية الحالية" else "لوحة مفاتيح أخرى نشطة",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = {
                            imm?.showInputMethodPicker()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDefault) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isDefault) "تبديل" else "اختيار",
                            fontSize = 11.sp,
                            color = if (isDefault) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                        )
                    }
                }
            }
        }

        // Interactive Sandbox / Playground
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "جرب الكيبورد هنا (Playground)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "🔤 $wordsCount كلمة مكتوبة",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "اضغط على مربع النص أدناه لتجربة الكتابة بالعربية، الإنجليزية، التشكيل، الزخرفة، والحافظة فوراً:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = testInputText,
                    onValueChange = {
                        testInputText = it
                        wordsCount = prefs.wordsTypedCount
                    },
                    placeholder = { Text("اكتب هنا للتجربة...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                if (testInputText.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { testInputText = "" }) {
                            Text("مسح النص")
                        }
                    }
                }
            }
        }

        // Quick Navigation Grid
        Text(
            text = "أقسام التخصيص والميزات",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickNavCard(
                title = "لغات الكيبورد",
                subtitle = "العربية و40+ لغة عالمية",
                icon = Icons.Default.Language,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToLanguages
            )
            QuickNavCard(
                title = "الثيمات والمظهر",
                subtitle = "9 ثيمات وتخصيص كامل",
                icon = Icons.Default.Palette,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToThemes
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickNavCard(
                title = "الحافظة الدائمة",
                subtitle = "تثبيت وتصنيف النصوص",
                icon = Icons.Default.ContentPaste,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToClipboard
            )
            QuickNavCard(
                title = "اختصارات النصوص",
                subtitle = "توسيع العبارات تلقائياً",
                icon = Icons.Default.FlashOn,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToShortcuts
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickNavCard(
                title = "الإعدادات العامة",
                subtitle = "الصوت، الاهتزاز، الحجم، الاقتراحات",
                icon = Icons.Default.Settings,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToSettings
            )
        }
    }
}

@Composable
private fun FeatureBadge(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun QuickNavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
