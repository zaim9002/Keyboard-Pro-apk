package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*

enum class AppDestination(val title: String, val icon: ImageVector) {
    DASHBOARD("الرئيسية", Icons.Default.Home),
    LANGUAGES("اللغات", Icons.Default.Language),
    THEMES("الثيمات", Icons.Default.Palette),
    CLIPBOARD("الحافظة", Icons.Default.ContentPaste),
    SHORTCUTS("الاختصارات", Icons.Default.FlashOn),
    DICTIONARY("القاموس", Icons.Default.MenuBook),
    SETTINGS("الإعدادات", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var currentDestination by remember { mutableStateOf(AppDestination.DASHBOARD) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⌨️ ${currentDestination.title}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                tonalElevation = 6.dp
            ) {
                AppDestination.values().forEach { destination ->
                    val isSelected = currentDestination == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.title
                            )
                        },
                        label = {
                            Text(
                                text = destination.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Crossfade(targetState = currentDestination, label = "screen_transition") { destination ->
                when (destination) {
                    AppDestination.DASHBOARD -> DashboardScreen(
                        onNavigateToThemes = { currentDestination = AppDestination.THEMES },
                        onNavigateToClipboard = { currentDestination = AppDestination.CLIPBOARD },
                        onNavigateToShortcuts = { currentDestination = AppDestination.SHORTCUTS },
                        onNavigateToSettings = { currentDestination = AppDestination.SETTINGS },
                        onNavigateToLanguages = { currentDestination = AppDestination.LANGUAGES }
                    )
                    AppDestination.LANGUAGES -> LanguagesScreen()
                    AppDestination.THEMES -> ThemesScreen()
                    AppDestination.CLIPBOARD -> ClipboardScreen()
                    AppDestination.SHORTCUTS -> ShortcutsScreen()
                    AppDestination.DICTIONARY -> DictionaryScreen()
                    AppDestination.SETTINGS -> SettingsScreen(
                        onNavigateToLanguages = { currentDestination = AppDestination.LANGUAGES }
                    )
                }
            }
        }
    }
}
