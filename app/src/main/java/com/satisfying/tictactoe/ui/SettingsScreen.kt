package com.satisfying.tictactoe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.satisfying.tictactoe.audio.SoundManager
import com.satisfying.tictactoe.theme.NeonCoral
import com.satisfying.tictactoe.theme.NeonCyan

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var isMuted by remember { mutableStateOf(SoundManager.isAudioMuted()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07080F))
    ) {
        AnimatedGridBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(52.dp))

            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    fontSize = 24.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.clickable {
                        HapticEngine.click(context)
                        onNavigateBack()
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Settings",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Sound row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF10111A))
                    .padding(horizontal = 22.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Sound Effects",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = if (isMuted) "Off" else "On",
                        fontSize = 12.sp,
                        color = if (isMuted) Color.White.copy(alpha = 0.3f) else NeonCyan
                    )
                }

                Switch(
                    checked = !isMuted,
                    onCheckedChange = {
                        isMuted = SoundManager.toggleMute()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = NeonCyan,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color(0xFF1C1C2E)
                    )
                )
            }
        }
    }
}
