package com.satisfying.tictactoe.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.satisfying.tictactoe.GameState
import com.satisfying.tictactoe.Player
import com.satisfying.tictactoe.theme.ElectricGold
import com.satisfying.tictactoe.theme.NeonCoral
import com.satisfying.tictactoe.theme.NeonCyan
import com.satisfying.tictactoe.theme.SurfaceDark

@Composable
fun WinnerOverlay(
    gameState: GameState,
    onPlayAgain: () -> Unit,
    particleController: ParticleController,
    modifier: Modifier = Modifier
) {
    val isVisible = gameState !is GameState.Playing

    LaunchedEffect(gameState) {
        if (gameState is GameState.Won) {
            particleController.emitConfettiStorm(1080f, 1920f, count = 200)
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(250)),
        exit = fadeOut(tween(200)),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
                    + fadeIn(tween(300)),
                exit = scaleOut() + fadeOut(tween(150))
            ) {
                val winner = (gameState as? GameState.Won)?.result?.winner
                val isDraw = gameState is GameState.Draw
                val accentColor = when (winner) {
                    Player.X -> NeonCyan
                    Player.O -> NeonCoral
                    null -> ElectricGold
                }

                val infiniteTransition = rememberInfiniteTransition(label = "win_anim")
                val glowPulse by infiniteTransition.animateFloat(
                    initialValue = 0.5f, targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                    label = "winGlow"
                )
                val bounce by infiniteTransition.animateFloat(
                    initialValue = -6f, targetValue = 6f,
                    animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                    label = "bounce"
                )
                val emojiScale by infiniteTransition.animateFloat(
                    initialValue = 1f, targetValue = 1.25f,
                    animationSpec = infiniteRepeatable(tween(500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                    label = "emojiScale"
                )

                val (emoji, headline, subtext) = when {
                    isDraw -> Triple("🤝", "IT'S A DRAW!", "You're both legends...")
                    winner == Player.X -> Triple("🏆", "PLAYER X WINS!", "ABSOLUTELY BONKERS!")
                    else -> Triple("🤖", "PLAYER O WINS!", "RESISTANCE IS FUTILE!")
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(20.dp)
                        .graphicsLayer { translationY = bounce }
                        .shadow(
                            elevation = (32f * glowPulse).dp,
                            shape = CutCornerShape(20.dp),
                            ambientColor = accentColor,
                            spotColor = accentColor
                        )
                        .clip(CutCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF0D1117), SurfaceDark)
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(listOf(accentColor, accentColor.copy(alpha = 0.2f))),
                            shape = CutCornerShape(20.dp)
                        )
                        .padding(horizontal = 40.dp, vertical = 44.dp)
                ) {
                    // Huge animated emoji
                    Text(
                        text = emoji,
                        fontSize = 72.sp,
                        modifier = Modifier.graphicsLayer { scaleX = emojiScale; scaleY = emojiScale }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = headline,
                        fontFamily = FontFamily.Monospace,
                        color = accentColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = subtext,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    // Play Again — uses our juicy button
                    JuicyMenuButton(
                        label = "▶  PLAY AGAIN",
                        onClick = onPlayAgain,
                        color = accentColor,
                        glowPulse = glowPulse,
                        isHero = true
                    )
                }
            }
        }
    }
}
