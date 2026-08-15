package com.satisfying.tictactoe.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val radius: Float,
    var alpha: Float = 1f,
    val lifeDecay: Float = Random.nextFloat() * 0.02f + 0.015f,
    val isConfetti: Boolean = false,
    var rotation: Float = 0f,
    var rotationSpeed: Float = (Random.nextFloat() - 0.5f) * 15f
)

class ParticleController {
    val particles = mutableStateListOf<Particle>()

    fun emitSparkBurst(center: Offset, color: Color, count: Int = 24) {
        for (i in 0 until count) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 12f + 4f
            particles.add(
                Particle(
                    x = center.x,
                    y = center.y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    color = color,
                    radius = Random.nextFloat() * 6f + 4f,
                    lifeDecay = Random.nextFloat() * 0.03f + 0.02f
                )
            )
        }
    }

    fun emitConfettiStorm(width: Float, height: Float, count: Int = 70) {
        val colors = listOf(
            Color(0xFF00F5D4), // Bright Teal
            Color(0xFFFF007F), // Neon Pink
            Color(0xFFFFD166), // Gold
            Color(0xFF7000FF), // Electric Purple
            Color(0xFF00BBF9)  // Sky Blue
        )
        for (i in 0 until count) {
            val startX = Random.nextFloat() * width
            val startY = Random.nextFloat() * (height * 0.3f)
            val angle = (Random.nextFloat() * 0.8f + 0.1f) * Math.PI.toFloat() // downward spray
            val speed = Random.nextFloat() * 14f + 6f
            particles.add(
                Particle(
                    x = startX,
                    y = startY,
                    vx = cos(angle) * speed * (if (Random.nextBoolean()) 1f else -1f),
                    vy = sin(angle) * speed + 4f,
                    color = colors.random(),
                    radius = Random.nextFloat() * 8f + 6f,
                    lifeDecay = Random.nextFloat() * 0.008f + 0.006f,
                    isConfetti = true
                )
            )
        }
    }

    fun update() {
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val p = iterator.next()
            p.x += p.vx
            p.y += p.vy
            p.vy += if (p.isConfetti) 0.3f else 0.15f // gravity
            p.vx *= 0.96f // drag
            p.alpha -= p.lifeDecay
            p.rotation += p.rotationSpeed

            if (p.alpha <= 0f) {
                iterator.remove()
            }
        }
    }
}

@Composable
fun ParticleHost(
    controller: ParticleController,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(controller) {
        while (true) {
            withFrameNanos {
                controller.update()
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        controller.particles.forEach { p ->
            if (p.alpha > 0f) {
                drawParticle(p)
            }
        }
    }
}

private fun DrawScope.drawParticle(p: Particle) {
    val particleColor = p.color.copy(alpha = p.alpha.coerceIn(0f, 1f))
    if (p.isConfetti) {
        // Draw spinning rectangular confetti
        val size = p.radius * 2f
        drawCircle(
            color = particleColor,
            radius = p.radius,
            center = Offset(p.x, p.y)
        )
    } else {
        // Glowing radial spark
        drawCircle(
            color = particleColor.copy(alpha = (p.alpha * 0.3f).coerceIn(0f, 1f)),
            radius = p.radius * 2.2f,
            center = Offset(p.x, p.y)
        )
        drawCircle(
            color = particleColor,
            radius = p.radius,
            center = Offset(p.x, p.y)
        )
    }
}
