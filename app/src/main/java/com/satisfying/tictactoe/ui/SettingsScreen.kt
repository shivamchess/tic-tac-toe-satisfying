package com.satisfying.tictactoe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
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
    var isMuted by remember { mutableStateOf(SoundManager.isAudioMuted()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF02000A))
    ) {
        AnimatedGridBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "SYS.SETTINGS",
                fontFamily = FontFamily.Monospace,
                color = Color.White,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontSize = 24.sp
            )
            
            Spacer(modifier = Modifier.height(60.dp))

            // Sound Toggle Setting
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, CutCornerShape(12.dp), ambientColor = NeonCyan, spotColor = NeonCyan)
                    .clip(CutCornerShape(12.dp))
                    .background(Color(0x33000000))
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), CutCornerShape(12.dp))
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "> AUDIO_SYSTEM",
                    fontFamily = FontFamily.Monospace,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Switch(
                    checked = !isMuted,
                    onCheckedChange = {
                        isMuted = SoundManager.toggleMute()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = NeonCyan,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.DarkGray
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Back Button
            MenuButton(
                label = "< RETURN",
                onClick = onNavigateBack,
                color = NeonCoral
            )
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
