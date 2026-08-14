package com.satisfying.tictactoe.ui

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.satisfying.tictactoe.GameState
import com.satisfying.tictactoe.Player

@Composable
fun WinnerOverlay(
    gameState: GameState,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val isVisible = gameState !is GameState.Playing

    LaunchedEffect(gameState) {
        if (gameState is GameState.Won) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        } else if (gameState is GameState.Draw) {
            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(500)),
        exit = fadeOut(tween(300)),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .animateEnterExit(
                        enter = scaleIn(tween(500, delayMillis = 100))
                    )
            ) {
                Text(
                    text = when (gameState) {
                        is GameState.Won -> "Player ${gameState.result.winner.name} Wins!"
                        is GameState.Draw -> "It's a Draw!"
                        else -> ""
                    },
                    style = MaterialTheme.typography.displayLarge,
                    color = when (gameState) {
                        is GameState.Won -> if (gameState.result.winner == Player.X) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onPlayAgain,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Play Again",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
