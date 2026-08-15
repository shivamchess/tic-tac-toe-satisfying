package com.satisfying.tictactoe.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.satisfying.tictactoe.GameState
import com.satisfying.tictactoe.Player
import com.satisfying.tictactoe.theme.ElectricGold
import com.satisfying.tictactoe.theme.NeonCoral
import com.satisfying.tictactoe.theme.NeonCyan
import com.satisfying.tictactoe.theme.SurfaceDark
import com.satisfying.tictactoe.theme.SurfaceElevated

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
            particleController.emitConfettiStorm(1080f, 1920f, count = 120)
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(200)),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.78f)),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
                exit = scaleOut()
            ) {
                val winner = (gameState as? GameState.Won)?.result?.winner
                val isDraw = gameState is GameState.Draw
                val accentColor = when (winner) {
                    Player.X -> NeonCyan
                    Player.O -> NeonCoral
                    null -> ElectricGold
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(24.dp)
                        .shadow(
                            elevation = 24.dp,
                            shape = RoundedCornerShape(32.dp),
                            ambientColor = accentColor,
                            spotColor = accentColor
                        )
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(SurfaceElevated, SurfaceDark)
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                listOf(accentColor, accentColor.copy(alpha = 0.2f))
                            ),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .padding(horizontal = 36.dp, vertical = 40.dp)
                ) {
                    Text(
                        text = if (isDraw) "🤝" else if (winner == Player.X) "👑" else "🤖",
                        fontSize = 54.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = when (gameState) {
                            is GameState.Won -> "PLAYER ${gameState.result.winner.name} WINS!"
                            is GameState.Draw -> "IT'S A DRAW!"
                            else -> ""
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        color = accentColor,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isDraw) "Well matched battle!" else "Flawless victory!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onPlayAgain,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .height(56.dp)
                            .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = accentColor)
                    ) {
                        Text(
                            text = "PLAY AGAIN",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            letterSpacing = 1.2.sp,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            }
        }
    }
}
