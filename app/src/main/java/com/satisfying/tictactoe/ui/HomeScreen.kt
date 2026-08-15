package com.satisfying.tictactoe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.satisfying.tictactoe.theme.NeonCyan

@Composable
fun HomeScreen(
    onNavigateToGame: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
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
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SYS.TIC_TAC_TOE",
                fontFamily = FontFamily.Monospace,
                color = Color.White,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontSize = 32.sp
            )
            Text(
                text = "V.3D_HOLO_EDITION",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(80.dp))

            MenuButton(
                label = "> START_GAME",
                onClick = onNavigateToGame,
                color = NeonCyan
            )

            Spacer(modifier = Modifier.height(24.dp))

            MenuButton(
                label = "> SETTINGS",
                onClick = onNavigateToSettings,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
fun MenuButton(
    label: String,
    onClick: () -> Unit,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(60.dp)
            .shadow(
                elevation = 8.dp,
                shape = CutCornerShape(12.dp),
                ambientColor = color,
                spotColor = color
            )
            .clip(CutCornerShape(12.dp))
            .background(Color(0x33000000))
            .border(1.dp, color.copy(alpha = 0.6f), CutCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            letterSpacing = 2.sp
        )
    }
}
