package com.satisfying.tictactoe.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.satisfying.tictactoe.theme.NeonCyan

@Composable
fun AnimatedGridBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "grid_transition")
    val gridOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "grid_offset"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Horizon line is 30% from the top
        val horizonY = height * 0.3f
        
        // Draw deep space background with subtle gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF02000A), // Deep void
                    Color(0xFF0F0529), // Dark purple
                    Color(0xFF02000A)
                ),
                startY = 0f,
                endY = height
            )
        )

        // Draw a glowing sun/horizon
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    NeonCyan.copy(alpha = 0.0f),
                    NeonCyan.copy(alpha = 0.15f),
                    NeonCyan.copy(alpha = 0.0f)
                ),
                startY = horizonY - 100f,
                endY = horizonY + 50f
            ),
            topLeft = Offset(0f, horizonY - 100f),
            size = androidx.compose.ui.geometry.Size(width, 150f)
        )

        val gridColor = NeonCyan.copy(alpha = 0.3f)
        val center = width / 2f
        val numVerticalLines = 14
        val spread = width * 2.5f

        // Draw Perspective Vertical Lines
        for (i in -numVerticalLines..numVerticalLines) {
            val startX = center
            val endX = center + (i * (spread / numVerticalLines))
            
            drawLine(
                color = gridColor,
                start = Offset(startX, horizonY),
                end = Offset(endX, height),
                strokeWidth = 2f
            )
        }

        // Draw Perspective Horizontal Lines moving towards camera
        val numHorizontalLines = 15
        for (i in 0..numHorizontalLines) {
            val virtualIndex = i + gridOffset
            val progress = virtualIndex / numHorizontalLines
            val perspectiveProgress = progress * progress * progress
            val y = horizonY + (height - horizonY) * perspectiveProgress
            val alpha = (perspectiveProgress * 1.5f).coerceIn(0f, 0.4f)
            
            if (y > horizonY && y < height) {
                drawLine(
                    color = NeonCyan.copy(alpha = alpha),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f + (perspectiveProgress * 3f)
                )
            }
        }
    }
}
