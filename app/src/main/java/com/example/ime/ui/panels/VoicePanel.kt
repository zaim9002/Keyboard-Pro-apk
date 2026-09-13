package com.example.ime.ui.panels

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme

@Composable
fun VoicePanel(
    modifier: Modifier = Modifier,
    isListening: Boolean,
    statusText: String,
    partialResult: String,
    colorScheme: KeyboardColorScheme,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onClose: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🎤 الكتابة بالصوت",
                color = colorScheme.accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "إغلاق",
                    tint = colorScheme.keyText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Center visualizer & animated mic
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(if (isListening) colorScheme.accent else colorScheme.keyBackground),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        if (isListening) onStopListening() else onStartListening()
                    }
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "ميكروفون",
                        tint = if (isListening) androidx.compose.ui.graphics.Color.White else colorScheme.keyText,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isListening) "جارٍ الاستماع... تكلّم الآن" else statusText,
                color = colorScheme.keyText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            if (partialResult.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = partialResult,
                    color = colorScheme.accent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Bottom Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (isListening) onStopListening() else onStartListening()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isListening) colorScheme.specialKeyBackground else colorScheme.accent
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isListening) "إيقاف الاستماع" else "ابدأ التحدث",
                    fontSize = 12.sp,
                    color = if (isListening) colorScheme.specialKeyText else androidx.compose.ui.graphics.Color.White
                )
            }
        }
    }
}
