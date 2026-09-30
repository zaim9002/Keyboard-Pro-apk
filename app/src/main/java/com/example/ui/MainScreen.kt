package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*

enum class AppDestination(val title: String, val icon: ImageVector) {
    TEST("معاينة", Icons.Default.Keyboard),
    SETTINGS("الإعدادات", Icons.Default.Settings),
    MY_THEMES("سماتي", Icons.Default.Person),
    COLOR("لون", Icons.Default.Palette),
    DESIGN("تصميم", Icons.Default.Favorite),
    PHOTO_GIF("صورة", Icons.Default.Collections)
}

enum class SubScreen {
    LANGUAGES,
    SMART_INPUT,
    SHORTCUTS,
    DICTIONARY,
    CLIPBOARD
}

@Composable
fun MainScreen() {
    var currentDestination by remember { mutableStateOf(AppDestination.TEST) }
    var currentSubScreen by remember { mutableStateOf<SubScreen?>(null) }

    // Intercept back button so it always returns to previous screen instead of exiting the app
    val shouldInterceptBack = currentSubScreen != null || currentDestination != AppDestination.TEST
    BackHandler(enabled = shouldInterceptBack) {
        if (currentSubScreen != null) {
            currentSubScreen = null
        } else if (currentDestination != AppDestination.TEST) {
            currentDestination = AppDestination.TEST
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            bottomBar = {
                // Hide bottom navigation bar when navigating inside a sub-screen
                if (currentSubScreen == null) {
                    Surface(
                        color = Color(0xFF18181C),
                        tonalElevation = 8.dp,
                        shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent,
                            modifier = Modifier.fillMaxWidth(),
                            windowInsets = NavigationBarDefaults.windowInsets
                        ) {
                            AppDestination.values().forEach { destination ->
                                val isSelected = currentDestination == destination
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        currentSubScreen = null
                                        currentDestination = destination
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = destination.icon,
                                            contentDescription = destination.title,
                                            tint = if (isSelected) Color(0xFF818CF8) else Color(0xFF9E9EA4),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = destination.title,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color(0xFF818CF8) else Color(0xFF9E9EA4),
                                            maxLines = 1,
                                            textAlign = TextAlign.Center
                                        )
                                    },
                                    alwaysShowLabel = true,
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color(0xFF818CF8).copy(alpha = 0.2f),
                                        selectedIconColor = Color(0xFF818CF8),
                                        selectedTextColor = Color(0xFF818CF8),
                                        unselectedIconColor = Color(0xFF9E9EA4),
                                        unselectedTextColor = Color(0xFF9E9EA4)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF141416))
                    .padding(paddingValues)
            ) {
                // If a subscreen is active, render it directly
                if (currentSubScreen != null) {
                    when (currentSubScreen) {
                        SubScreen.LANGUAGES -> LanguagesScreen(
                            onBack = { currentSubScreen = null }
                        )
                        SubScreen.SMART_INPUT -> SmartInputScreen(
                            onBack = { currentSubScreen = null }
                        )
                        SubScreen.SHORTCUTS -> ShortcutsScreen(
                            onBack = { currentSubScreen = null }
                        )
                        SubScreen.DICTIONARY -> DictionaryScreen(
                            onBack = { currentSubScreen = null }
                        )
                        SubScreen.CLIPBOARD -> ClipboardScreen(
                            onBack = { currentSubScreen = null }
                        )
                        null -> {}
                    }
                } else {
                    Crossfade(targetState = currentDestination, label = "screen_transition") { destination ->
                        when (destination) {
                            AppDestination.TEST -> InstallFinishScreen(
                                onNavigateToSettings = { currentDestination = AppDestination.SETTINGS },
                                onNavigateToThemes = { currentDestination = AppDestination.COLOR }
                            )
                            AppDestination.PHOTO_GIF -> PhotoGifThemesScreen()
                            AppDestination.DESIGN -> DesignThemesScreen()
                            AppDestination.COLOR -> ColorThemesScreen()
                            AppDestination.MY_THEMES -> MyThemesScreen()
                            AppDestination.SETTINGS -> SettingsScreen(
                                onNavigateToThemes = { currentDestination = AppDestination.COLOR },
                                onNavigateToLanguages = { currentSubScreen = SubScreen.LANGUAGES },
                                onNavigateToSmartInput = { currentSubScreen = SubScreen.SMART_INPUT },
                                onNavigateToShortcuts = { currentSubScreen = SubScreen.SHORTCUTS },
                                onNavigateToDictionary = { currentSubScreen = SubScreen.DICTIONARY },
                                onNavigateToClipboard = { currentSubScreen = SubScreen.CLIPBOARD },
                                onBack = { currentDestination = AppDestination.TEST }
                            )
                        }
                    }
                }
            }
        }
    }
}
