package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.KeyboardProApp
import com.example.language.download.DownloadResult
import com.example.language.model.LanguageInfo
import com.example.language.model.LanguagePackStatus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagesScreen() {
    val app = KeyboardProApp.instance
    val langManager = app.languageManager
    val prefs = app.preferences
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val allLanguages by langManager.repository.getAllLanguages().collectAsState(initial = emptyList())
    val activeDownloads by langManager.downloadManager.activeDownloads.collectAsState()
    val downloadProgress by langManager.downloadManager.downloadProgress.collectAsState()
    var enabledLangs by remember { mutableStateOf(prefs.enabledLanguages) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) } // 0: Installed, 1: All Languages

    val filteredLanguages = remember(allLanguages, searchQuery, selectedTab) {
        val list = when (selectedTab) {
            0 -> allLanguages.filter { it.status == LanguagePackStatus.INSTALLED || it.status == LanguagePackStatus.UPDATE_AVAILABLE || it.id == "ar" }
            else -> allLanguages
        }
        if (searchQuery.isBlank()) {
            list
        } else {
            val q = searchQuery.trim().lowercase()
            list.filter {
                it.nameArabic.lowercase().contains(q) ||
                        it.nativeName.lowercase().contains(q) ||
                        it.id.lowercase().contains(q)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Info Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "مدير لغات الكيبورد العالمية",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "العربية مدمجة وأساسية دائماً بدون إنترنت. يمكنك تحميل أكثر من 40 لغة عالمية واستخدامها دون اتصال بعد التنزيل.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("ابحث عن لغة (مثال: الفرنسية، Русский، Español)...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "مسح")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Tabs
            val installedCount = allLanguages.count { it.status == LanguagePackStatus.INSTALLED || it.status == LanguagePackStatus.UPDATE_AVAILABLE || it.id == "ar" }
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("المثبتة ($installedCount)", fontSize = 13.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("جميع اللغات (${allLanguages.size})", fontSize = 13.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
            }

            // List of Languages
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredLanguages, key = { it.id }) { lang ->
                    val isDownloading = activeDownloads.contains(lang.id)
                    val progress = downloadProgress[lang.id] ?: 0f
                    val isEnabled = enabledLangs.contains(lang.id) || lang.id == "ar"

                    LanguageCard(
                        language = lang,
                        isDownloading = isDownloading,
                        downloadProgress = progress,
                        isEnabled = isEnabled,
                        onToggleEnable = { enabled ->
                            val updated = if (enabled) {
                                enabledLangs + lang.id
                            } else {
                                if (lang.id != "ar") enabledLangs - lang.id else enabledLangs
                            }
                            enabledLangs = updated
                            prefs.enabledLanguages = updated
                        },
                        onDownload = {
                            coroutineScope.launch {
                                langManager.packManager.install(
                                    language = lang,
                                    onComplete = { result ->
                                        when (result) {
                                            is DownloadResult.Success -> {
                                                coroutineScope.launch {
                                                    // Automatically enable newly installed language
                                                    val updated = enabledLangs + lang.id
                                                    enabledLangs = updated
                                                    prefs.enabledLanguages = updated
                                                    snackbarHostState.showSnackbar("تم تثبيت حزمة ${lang.nameArabic} بنجاح!")
                                                }
                                            }
                                            is DownloadResult.Error -> {
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(result.message)
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        },
                        onCancelDownload = {
                            langManager.packManager.cancel(lang.id)
                        },
                        onDelete = {
                            val success = langManager.packManager.delete(lang.id)
                            if (success) {
                                val updated = enabledLangs - lang.id
                                enabledLangs = updated
                                prefs.enabledLanguages = updated
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("تمت إزالة حزمة ${lang.nameArabic}")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageCard(
    language: LanguageInfo,
    isDownloading: Boolean,
    downloadProgress: Float,
    isEnabled: Boolean,
    onToggleEnable: (Boolean) -> Unit,
    onDownload: () -> Unit,
    onCancelDownload: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Flag
                Text(
                    text = language.flag,
                    fontSize = 28.sp
                )

                // Names
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = language.nameArabic,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        if (language.isBuiltIn) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "مدمجة وأساسية",
                                    color = Color(0xFF059669),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "${language.nativeName} • ${language.sizeMb} ميغابايت",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Action area based on status
                if (isDownloading) {
                    IconButton(onClick = onCancelDownload) {
                        Icon(Icons.Default.Close, contentDescription = "إلغاء التحميل", tint = MaterialTheme.colorScheme.error)
                    }
                } else if (language.status == LanguagePackStatus.INSTALLED || language.id == "ar") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (!language.isBuiltIn) {
                            IconButton(onClick = onDelete) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "حذف الحزمة", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                            }
                        }
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = onToggleEnable,
                            enabled = language.id != "ar" // Arabic is always enabled
                        )
                    }
                } else {
                    Button(
                        onClick = onDownload,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language.status == LanguagePackStatus.UPDATE_AVAILABLE) "تحديث" else "تحميل",
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Download Progress indicator
            if (isDownloading) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("جاري تنزيل حزمة اللغة...", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        Text("${(downloadProgress * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    LinearProgressIndicator(
                        progress = { downloadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                }
            }
        }
    }
}
