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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.satisfying.tictactoe.GameMode
import com.satisfying.tictactoe.Player
import com.satisfying.tictactoe.theme.ElectricGold
import com.satisfying.tictactoe.theme.NeonCoral
import com.satisfying.tictactoe.theme.NeonCyan
import com.satisfying.tictactoe.theme.SurfaceDark
import com.satisfying.tictactoe.theme.SurfaceElevated

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
        // Win Streak Badge
        if (streak > 1) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(ElectricGold.copy(alpha = 0.2f), NeonCoral.copy(alpha = 0.2f))))
                    .border(1.dp, ElectricGold.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🔥 $streak WIN STREAK!",
                    color = ElectricGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerScoreCard(
                playerName = "Player X",
                mark = "X",
                score = scoreX,
                isActive = currentPlayer == Player.X,
                accentColor = NeonCyan
            )

            Text(
                text = "VS",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White.copy(alpha = 0.3f),
                fontWeight = FontWeight.Black
            )

            PlayerScoreCard(
                playerName = when (gameMode) {
                    GameMode.TWO_PLAYER -> "Player O"
                    GameMode.AI_EASY -> "AI (Easy)"
                    GameMode.AI_IMPOSSIBLE -> "AI (Boss)"
                },
                mark = "O",
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
    mark: String,
    score: Int,
    isActive: Boolean,
    accentColor: Color,
    isThinking: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer {
                if (isActive) {
                    scaleX = scale
                    scaleY = scale
                }
            }
            .width(135.dp)
            .shadow(
                elevation = if (isActive) 14.dp else 4.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = if (isActive) accentColor else Color.Transparent,
                spotColor = if (isActive) accentColor else Color.Transparent
            )
            .clip(RoundedCornerShape(22.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isActive) listOf(
                        SurfaceElevated,
                        accentColor.copy(alpha = 0.15f)
                    ) else listOf(
                        SurfaceDark,
                        Color(0xFF0B0E14)
                    )
                )
            )
            .border(
                width = if (isActive) 1.8.dp else 1.dp,
                color = if (isActive) accentColor.copy(alpha = glowAlpha) else Color(0x22FFFFFF),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Text(
            text = if (isThinking) "Thinking..." else playerName,
            style = MaterialTheme.typography.labelLarge,
            color = if (isActive) accentColor else Color(0xFF94A3B8),
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(4.dp))

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
                text = targetScore.toString(),
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 42.sp),
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Active Turn Glowing indicator
        Box(
            modifier = Modifier
                .size(width = 30.dp, height = 4.dp)
                .clip(RoundedCornerShape(50))
                .background(if (isActive) accentColor.copy(alpha = glowAlpha) else Color.Transparent)
        )
    }
}
