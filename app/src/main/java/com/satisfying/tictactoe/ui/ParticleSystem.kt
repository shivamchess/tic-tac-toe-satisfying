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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
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
    val isStar: Boolean = false,
    var rotation: Float = Random.nextFloat() * 360f,
    var rotationSpeed: Float = (Random.nextFloat() - 0.5f) * 20f
)

class ParticleController {
    val particles = mutableStateListOf<Particle>()

    fun emitSparkBurst(center: Offset, color: Color, count: Int = 32) {
        for (i in 0 until count) {
            val angle = (i.toFloat() / count) * 2f * Math.PI.toFloat() + Random.nextFloat() * 0.5f
            val speed = Random.nextFloat() * 14f + 6f
            particles.add(
                Particle(
                    x = center.x,
                    y = center.y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    color = color,
                    radius = Random.nextFloat() * 7f + 5f,
                    lifeDecay = Random.nextFloat() * 0.025f + 0.018f
                )
            )
        }
        // Extra fast tiny sparks for detail
        for (i in 0 until 12) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 22f + 10f
            particles.add(
                Particle(
                    x = center.x,
                    y = center.y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    color = Color.White,
                    radius = Random.nextFloat() * 3f + 2f,
                    lifeDecay = Random.nextFloat() * 0.04f + 0.03f
                )
            )
        }
    }

    fun emitConfettiStorm(width: Float, height: Float, count: Int = 200) {
        val colors = listOf(
            Color(0xFF00F5D4), // Bright Teal
            Color(0xFFFF007F), // Neon Pink
            Color(0xFFFFD166), // Gold
            Color(0xFF7000FF), // Electric Purple
            Color(0xFF00BBF9), // Sky Blue
            Color(0xFFFF4F00), // Orange
            Color(0xFF39FF14), // Neon Green
            Color.White
        )
        for (i in 0 until count) {
            val startX = Random.nextFloat() * width
            val startY = -Random.nextFloat() * height * 0.3f // start above screen
            val vx = (Random.nextFloat() - 0.5f) * 10f
            val vy = Random.nextFloat() * 10f + 5f
            val useStar = Random.nextBoolean()
            particles.add(
                Particle(
                    x = startX,
                    y = startY,
                    vx = vx,
                    vy = vy,
                    color = colors.random(),
                    radius = Random.nextFloat() * 9f + 5f,
                    lifeDecay = Random.nextFloat() * 0.006f + 0.004f,
                    isConfetti = !useStar,
                    isStar = useStar
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
            p.vy += if (p.isConfetti || p.isStar) 0.25f else 0.18f // gravity
            p.vx *= 0.97f // drag
            p.vy *= 0.995f
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
    when {
        p.isStar -> {
            // Draw spinning 4-pointed star
            rotate(p.rotation, pivot = Offset(p.x, p.y)) {
                val r = p.radius
                val innerR = r * 0.4f
                val points = 4
                val path = Path()
                for (i in 0 until points * 2) {
                    val angle = (i * Math.PI / points - Math.PI / 2).toFloat()
                    val radius = if (i % 2 == 0) r else innerR
                    val px = p.x + cos(angle) * radius
                    val py = p.y + sin(angle) * radius
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close()
                drawPath(path, color = particleColor)
                drawPath(path, color = Color.White.copy(alpha = p.alpha * 0.4f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f))
            }
        }
        p.isConfetti -> {
            // Spinning rectangle confetti
            rotate(p.rotation, pivot = Offset(p.x, p.y)) {
                drawRect(
                    color = particleColor,
                    topLeft = Offset(p.x - p.radius, p.y - p.radius * 0.5f),
                    size = androidx.compose.ui.geometry.Size(p.radius * 2f, p.radius)
                )
            }
        }
        else -> {
            // Glowing radial spark with hot white core
            drawCircle(
                color = particleColor.copy(alpha = (p.alpha * 0.25f).coerceIn(0f, 1f)),
                radius = p.radius * 3f,
                center = Offset(p.x, p.y)
            )
            drawCircle(
                color = particleColor,
                radius = p.radius,
                center = Offset(p.x, p.y)
            )
            drawCircle(
                color = Color.White.copy(alpha = p.alpha * 0.8f),
                radius = p.radius * 0.4f,
                center = Offset(p.x, p.y)
            )
        }
    }
}
