package com.satisfying.tictactoe.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.satisfying.tictactoe.theme.NeonCoral
import com.satisfying.tictactoe.theme.NeonCyan
import com.satisfying.tictactoe.theme.ElectricGold
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(
    onNavigateToGame: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "home_anim")

    // Rotating rainbow hue for title
    val hueShift by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "hue"
    )

    // Pulsing scale for the title
    val titlePulse by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.07f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "titlePulse"
    )

    // Floating bob
    val bobOffset by infiniteTransition.animateFloat(
        initialValue = -12f, targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bob"
    )

    // Rotating stars in the background
    val starRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "starRotation"
    )

    // Glow pulse for buttons
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glowPulse"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF02000A))) {
        AnimatedGridBackground()

        // Spinning decorative stars layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height * 0.38f
            val numStars = 8
            val orbitRadius = size.width * 0.38f
            for (i in 0 until numStars) {
                val angle = (starRotation + i * (360f / numStars)) * (Math.PI / 180f)
                val sx = centerX + cos(angle).toFloat() * orbitRadius
                val sy = centerY + sin(angle).toFloat() * (orbitRadius * 0.35f)
                val starColor = when (i % 4) {
                    0 -> NeonCyan
                    1 -> NeonCoral
                    2 -> ElectricGold
                    else -> Color(0xFFAA44FF)
                }
                // Draw mini 4-pointed star
                drawCircle(starColor.copy(alpha = 0.6f), radius = 6f, center = Offset(sx, sy))
                drawCircle(Color.White.copy(alpha = 0.9f), radius = 2.5f, center = Offset(sx, sy))
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // MASSIVE animated title
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = titlePulse
                        scaleY = titlePulse
                        translationY = bobOffset
                    },
                contentAlignment = Alignment.Center
            ) {
                // Glow shadow behind title
                Text(
                    text = "TIC TAC TOE",
                    fontFamily = FontFamily.Monospace,
                    color = NeonCyan.copy(alpha = 0.3f),
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    fontSize = 40.sp,
                    modifier = Modifier.graphicsLayer { translationX = 4f; translationY = 4f }
                )
                // Gradient title using canvas trick
                Text(
                    text = "TIC TAC TOE",
                    fontFamily = FontFamily.Monospace,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    fontSize = 40.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "✦ ULTRA SATISFYING EDITION ✦",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = ElectricGold,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            Text(
                text = "★ THE GAME ★",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = NeonCoral.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(64.dp))

            // PLAY button — big, juicy, animated
            JuicyMenuButton(
                label = "▶  PLAY NOW",
                onClick = {
                    HapticEngine.click(context)
                    onNavigateToGame()
                },
                color = NeonCyan,
                glowPulse = glowPulse,
                isHero = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            JuicyMenuButton(
                label = "⚙  SETTINGS",
                onClick = {
                    HapticEngine.click(context)
                    onNavigateToSettings()
                },
                color = NeonCoral,
                glowPulse = glowPulse * 0.6f,
                isHero = false
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Corny tagline
            Text(
                text = "\"The world's most satisfying Tic Tac Toe\"",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.3f),
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun JuicyMenuButton(
    label: String,
    onClick: () -> Unit,
    color: Color,
    glowPulse: Float,
    isHero: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "btnScale"
    )

    val buttonHeight = if (isHero) 70.dp else 56.dp
    val fontSize = if (isHero) 18.sp else 14.sp
    val widthFraction = if (isHero) 0.82f else 0.65f

    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(buttonHeight)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(
                elevation = (20f * glowPulse).dp,
                shape = CutCornerShape(14.dp),
                ambientColor = color,
                spotColor = color
            )
            .clip(CutCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(color.copy(alpha = 0.25f), color.copy(alpha = 0.08f)),
                    start = Offset.Zero,
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .drawBehind {
                // Animated bright top-left bevel
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.25f), Color.Transparent),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    ),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx())
                )
                // Glowing border
                drawRoundRect(
                    color = color.copy(alpha = glowPulse),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = if (isHero) 2.5f else 1.5f)
                )
            }
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            color = color,
            fontWeight = FontWeight.Black,
            fontSize = fontSize,
            letterSpacing = 3.sp
        )
    }
}

// Re-export MenuButton for use from SettingsScreen
@Composable
fun MenuButton(label: String, onClick: () -> Unit, color: Color) {
    JuicyMenuButton(label = label, onClick = onClick, color = color, glowPulse = 0.6f, isHero = false)
}
