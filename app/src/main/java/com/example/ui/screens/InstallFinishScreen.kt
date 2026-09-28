package com.example.ui.screens

import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
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
import com.example.ime.theme.KeyboardThemes
import com.example.ime.ui.KeyboardScreen

@Composable
fun InstallFinishScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToThemes: () -> Unit
) {
    val prefs = KeyboardProApp.instance.preferences
    var testText by remember { mutableStateOf("") }
    var currentLanguage by remember { mutableStateOf(prefs.currentLanguage) }
    val colorScheme = remember(prefs.theme) { KeyboardThemes.getColorScheme(prefs.theme) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF161618))
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar with Settings gear
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "الإعدادات",
                    tint = Color.LightGray,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Mascot & Congratulatory Header (Scrollable to guarantee responsiveness on any screen size)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Cute Mascot Card
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFE4E6)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🧸💖", fontSize = 32.sp)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "اكتملت إعدادات لوحة\nمفاتيح التصميم!",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "اكتملت جميع الإعدادات!\nلنجرّب معًا لوحة المفاتيح التي اخترتها",
                color = Color.LightGray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(Modifier.height(10.dp))

            // Test Input Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF222226))
                    .border(1.dp, Color(0xFF333338), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (testText.isEmpty()) {
                        Text(
                            text = "جرّب اختبار لوحة المفاتيح.",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    } else {
                        Text(
                            text = testText,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }

                if (testText.isNotEmpty()) {
                    IconButton(
                        onClick = { testText = "" },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "مسح",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Live interactive keyboard preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            KeyboardScreen(
                colorScheme = colorScheme,
                currentLanguage = currentLanguage,
                imeOptions = EditorInfo.IME_ACTION_DONE,
                isIncognito = false,
                keyboardHeight = prefs.keyboardHeight,
                showNumberRow = prefs.showNumberRow,
                hapticEnabled = prefs.hapticFeedback != "Off",
                soundEnabled = prefs.keySound != "Off",
                oneHandedMode = prefs.oneHandedMode,
                applyNavigationBarsPadding = false,
                suggestions = listOf("السلام", "شكراً", "تمام", "أهلاً"),
                clipboardList = emptyList(),
                isVoiceListening = false,
                voiceStatusText = "جاهز للاستماع",
                voicePartialText = "",
                onTextInput = { text ->
                    testText += text
                },
                onDelete = {
                    if (testText.isNotEmpty()) {
                        testText = testText.dropLast(1)
                    }
                },
                onEnter = {
                    testText += "\n"
                },
                onSpace = {
                    testText += " "
                },
                onSwitchLanguage = {
                    currentLanguage = if (currentLanguage == "ar") "en" else "ar"
                },
                onMoveCursor = {},
                onSelectSuggestion = { sug ->
                    testText += "$sug "
                },
                onTogglePinClip = { _, _ -> },
                onDeleteClip = {},
                onClearUnpinnedClips = {},
                onStartVoice = {},
                onStopVoice = {},
                onSelectAll = {},
                onCut = { testText = "" },
                onCopy = {},
                onPaste = {},
                onUndo = { if (testText.isNotEmpty()) testText = testText.dropLast(1) },
                onRedo = {},
                onOpenSettings = onNavigateToSettings,
                onToggleOneHanded = {},
                currentDraftText = testText
            )
        }
    }
}
