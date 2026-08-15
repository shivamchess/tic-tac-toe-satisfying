package com.satisfying.tictactoe.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.satisfying.tictactoe.theme.ElectricGold
import com.satisfying.tictactoe.theme.NeonCoral
import com.satisfying.tictactoe.theme.NeonCyan
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(
    onNavigateToGame: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "home")

    // Slow rotation for the hero ring
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "rot"
    )

    // Gentle float
    val floatY by infiniteTransition.animateFloat(
        initialValue = -8f, targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float"
    )

    // Glow pulse
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF07080F))) {
        AnimatedGridBackground()

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Hero: animated X vs O canvas ──────────────────────────
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .graphicsLayer { translationY = floatY },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val outerR = size.width * 0.46f

                    // Rotating dashed orbit ring
                    val numDots = 24
                    for (i in 0 until numDots) {
                        val angle = (i.toFloat() / numDots) * 2f * Math.PI.toFloat() + rotation * (Math.PI / 180f).toFloat()
                        val dx = cos(angle) * outerR
                        val dy = sin(angle) * outerR
                        val dotAlpha = if (i % 3 == 0) 0.6f else 0.15f
                        drawCircle(
                            color = Color.White.copy(alpha = dotAlpha),
                            radius = if (i % 3 == 0) 3.5f else 2f,
                            center = Offset(cx + dx, cy + dy)
                        )
                    }

                    // O — left quadrant, violet
                    val oColor = NeonCyan
                    val oR = size.width * 0.18f
                    val oX = cx - size.width * 0.22f
                    val oY = cy
                    // Glow
                    drawCircle(oColor.copy(alpha = 0.15f), radius = oR * 1.8f, center = Offset(oX, oY))
                    // Stroke
                    drawCircle(color = oColor.copy(alpha = 0.25f), radius = oR,
                        center = Offset(oX, oY), style = Stroke(width = oR * 0.55f))
                    drawCircle(color = oColor, radius = oR,
                        center = Offset(oX, oY), style = Stroke(width = oR * 0.25f))
                    drawCircle(color = Color.White.copy(alpha = 0.7f), radius = oR,
                        center = Offset(oX, oY), style = Stroke(width = oR * 0.06f))

                    // X — right quadrant, rose
                    val xColor = NeonCoral
                    val xX = cx + size.width * 0.22f
                    val xY = cy
                    val pad = size.width * 0.08f
                    val xL = xX - pad; val xR = xX + pad
                    val xT = xY - pad; val xB = xY + pad
                    val sw = oR * 0.25f
                    val glowSw = oR * 0.55f

                    // glow
                    drawLine(xColor.copy(alpha = 0.2f), Offset(xL, xT), Offset(xR, xB), strokeWidth = glowSw, cap = StrokeCap.Round)
                    drawLine(xColor.copy(alpha = 0.2f), Offset(xR, xT), Offset(xL, xB), strokeWidth = glowSw, cap = StrokeCap.Round)
                    // core
                    drawLine(xColor, Offset(xL, xT), Offset(xR, xB), strokeWidth = sw, cap = StrokeCap.Round)
                    drawLine(xColor, Offset(xR, xT), Offset(xL, xB), strokeWidth = sw, cap = StrokeCap.Round)
                    // bright center
                    drawLine(Color.White.copy(alpha = 0.7f), Offset(xL, xT), Offset(xR, xB), strokeWidth = sw * 0.25f, cap = StrokeCap.Round)
                    drawLine(Color.White.copy(alpha = 0.7f), Offset(xR, xT), Offset(xL, xB), strokeWidth = sw * 0.25f, cap = StrokeCap.Round)
                }
            }
            // ─────────────────────────────────────────────────────────

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Tic Tac Toe",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = (-1).sp
            )
            Text(
                text = "Ultra Satisfying Edition",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = ElectricGold.copy(alpha = 0.85f),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(52.dp))

            // PLAY button — solid, pill-shaped, confident
            SolidButton(
                label = "Play",
                onClick = {
                    HapticEngine.click(context)
                    onNavigateToGame()
                },
                color = NeonCyan,
                isHero = true,
                glow = glow
            )

            Spacer(modifier = Modifier.height(16.dp))

            SolidButton(
                label = "Settings",
                onClick = {
                    HapticEngine.click(context)
                    onNavigateToSettings()
                },
                color = Color.White.copy(alpha = 0.15f),
                isHero = false,
                glow = 0f,
                textColor = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun SolidButton(
    label: String,
    onClick: () -> Unit,
    color: Color,
    isHero: Boolean,
    glow: Float,
    textColor: Color = Color.White
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(if (isHero) 0.85f else 0.65f)
            .height(if (isHero) 62.dp else 50.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .then(
                if (isHero) Modifier.shadow(
                    elevation = (12f * glow).dp,
                    shape = RoundedCornerShape(50),
                    ambientColor = color,
                    spotColor = color
                ) else Modifier
            )
            .clip(RoundedCornerShape(50))
            .background(color)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = if (isHero) 18.sp else 15.sp,
            fontWeight = if (isHero) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = textColor,
            letterSpacing = if (isHero) 0.5.sp else 0.sp
        )
    }
}

// Keep MenuButton for backward compat
@Composable
fun MenuButton(label: String, onClick: () -> Unit, color: Color) {
    SolidButton(label = label, onClick = onClick, color = color, isHero = false, glow = 0f)
}

// Keep JuicyMenuButton for WinnerOverlay compat
@Composable
fun JuicyMenuButton(
    label: String,
    onClick: () -> Unit,
    color: Color,
    glowPulse: Float,
    isHero: Boolean
) {
    SolidButton(label = label, onClick = onClick, color = color, isHero = isHero, glow = glowPulse)
}
