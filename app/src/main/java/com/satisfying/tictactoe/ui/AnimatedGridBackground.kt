package com.satisfying.tictactoe.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Star(
    val x: Float,
    val y: Float,
    val radius: Float,
    val speed: Float,
    val phase: Float
)

@Composable
fun AnimatedGridBackground(modifier: Modifier = Modifier) {
    // Generate stars once
    val stars = remember {
        (0 until 80).map {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 2.2f + 0.4f,
                speed = Random.nextFloat() * 0.4f + 0.1f,
                phase = Random.nextFloat() * 2f * Math.PI.toFloat()
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Rich deep background gradient — feels warm, not cold AI-blue
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF0E0C1A), // warm dark purple at center
                    Color(0xFF07080F)  // near-black at edges
                ),
                center = Offset(size.width * 0.5f, size.height * 0.3f),
                radius = size.width * 0.9f
            )
        )

        // Subtle bottom glow — makes the board area feel lit from below
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFF7C5CFC).copy(alpha = 0.04f),
                    Color(0xFF7C5CFC).copy(alpha = 0.08f)
                ),
                startY = size.height * 0.6f,
                endY = size.height
            )
        )

        // Slowly pulsing soft stars
        stars.forEach { star ->
            val pulse = (sin(time * star.speed + star.phase) * 0.5f + 0.5f)
            val alpha = (0.1f + pulse * 0.5f).coerceIn(0f, 0.6f)
            val radius = star.radius * (0.8f + pulse * 0.4f)
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = radius,
                center = Offset(star.x * size.width, star.y * size.height)
            )
        }
    }
}
