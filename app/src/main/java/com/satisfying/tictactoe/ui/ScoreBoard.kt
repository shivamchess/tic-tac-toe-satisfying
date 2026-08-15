package com.satisfying.tictactoe.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.satisfying.tictactoe.GameMode
import com.satisfying.tictactoe.Player
import com.satisfying.tictactoe.theme.ElectricGold
import com.satisfying.tictactoe.theme.NeonCoral
import com.satisfying.tictactoe.theme.NeonCyan

@Composable
fun ScoreBoard(
    scoreX: Int,
    scoreO: Int,
    streak: Int,
    currentPlayer: Player,
    gameMode: GameMode,
    isAiThinking: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Holographic Win Streak Badge
        if (streak > 1) {
            Box(
                modifier = Modifier
                    .clip(CutCornerShape(8.dp))
                    .background(Color(0x44000000))
                    .border(1.dp, Brush.horizontalGradient(listOf(ElectricGold, NeonCoral)), CutCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = ">>> $streak WIN STREAK <<<",
                    color = ElectricGold,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerScoreCard(
                playerName = "PLAYER 1",
                score = scoreX,
                isActive = currentPlayer == Player.X,
                accentColor = NeonCyan
            )

            Text(
                text = "// VS //",
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.3f),
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp
            )

            PlayerScoreCard(
                playerName = when (gameMode) {
                    GameMode.TWO_PLAYER -> "PLAYER 2"
                    GameMode.AI_EASY -> "AI.EASY"
                    GameMode.AI_IMPOSSIBLE -> "AI.BOSS"
                },
                score = scoreO,
                isActive = currentPlayer == Player.O,
                accentColor = NeonCoral,
                isThinking = isAiThinking && currentPlayer == Player.O
            )
        }
    }
}

@Composable
fun PlayerScoreCard(
    playerName: String,
    score: Int,
    isActive: Boolean,
    accentColor: Color,
    isThinking: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(130.dp)
            .shadow(
                elevation = if (isActive) 12.dp else 0.dp,
                shape = CutCornerShape(12.dp),
                ambientColor = if (isActive) accentColor else Color.Transparent,
                spotColor = if (isActive) accentColor else Color.Transparent
            )
            .clip(CutCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isActive) listOf(Color(0x66000000), accentColor.copy(alpha = 0.15f))
                    else listOf(Color(0x33000000), Color(0x33000000))
                )
            )
            .border(
                width = 1.dp,
                color = if (isActive) accentColor.copy(alpha = glowAlpha) else Color(0x22FFFFFF),
                shape = CutCornerShape(12.dp)
            )
            .padding(vertical = 12.dp, horizontal = 4.dp)
    ) {
        // Techy Status Label
        Text(
            text = if (isThinking) "> COMPUTING..." else "> $playerName",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = if (isActive) accentColor else Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedContent(
            targetState = score,
            transitionSpec = {
                (slideInVertically { height -> height } + fadeIn() togetherWith
                        slideOutVertically { height -> -height } + fadeOut())
                    .using(SizeTransform(clip = false))
            },
            label = "scoreRoll"
        ) { targetScore ->
            Text(
                text = targetScore.toString().padStart(2, '0'),
                fontFamily = FontFamily.Monospace,
                fontSize = 38.sp,
                color = if (isActive) Color.White else Color.White.copy(alpha = 0.5f),
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Active Turn Glowing indicator (Scanning line style)
        if (isActive) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(2.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, accentColor, Color.Transparent)
                        )
                    )
            )
        } else {
            Box(modifier = Modifier.height(2.dp))
        }
    }
}
