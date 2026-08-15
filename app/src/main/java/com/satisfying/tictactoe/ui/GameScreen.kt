package com.satisfying.tictactoe.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.satisfying.tictactoe.GameMode
import com.satisfying.tictactoe.GameState
import com.satisfying.tictactoe.GameViewModel
import com.satisfying.tictactoe.Player
import com.satisfying.tictactoe.theme.ElectricGold
import com.satisfying.tictactoe.theme.NeonCoral
import com.satisfying.tictactoe.theme.NeonCyan
import com.satisfying.tictactoe.theme.SurfaceDark
import com.satisfying.tictactoe.theme.SurfaceElevated

@Composable
fun GameScreen(
    onNavigateHome: () -> Unit,
    onNavigateLevelSelect: () -> Unit,
    initialGameMode: GameMode = GameMode.TWO_PLAYER,
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val particleController = remember { ParticleController() }
    val cellPositions = remember { mutableStateMapOf<Int, Offset>() }
    val context = androidx.compose.ui.platform.LocalContext.current

    // Apply the mode chosen on the Level Select screen
    LaunchedEffect(initialGameMode) {
        viewModel.setGameMode(initialGameMode)
    }

    // Fire haptics when game state changes (win / draw)
    LaunchedEffect(uiState.gameState) {
        when (uiState.gameState) {
            is GameState.Won -> HapticEngine.victoryRumble(context)
            is GameState.Draw -> HapticEngine.drawPulse(context)
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF02000A)) // Base dark color
    ) {
        // 1. Synthwave 3D Background layer
        AnimatedGridBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top App Bar with Holographic Title & Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back to home
                Text(
                    text = "←",
                    fontSize = 24.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable {
                            HapticEngine.click(context)
                            onNavigateHome()
                        }
                )

                Text(
                    text = "Tic Tac Toe",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Quick mode switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Change mode  ›",
                    fontSize = 12.sp,
                    color = ElectricGold.copy(alpha = 0.65f),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        HapticEngine.click(context)
                        onNavigateLevelSelect()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic ScoreBoard
            ScoreBoard(
                scoreX = uiState.scoreX,
                scoreO = uiState.scoreO,
                streak = uiState.streak,
                currentPlayer = uiState.currentPlayer,
                gameMode = uiState.gameMode,
                isAiThinking = uiState.isThinkingAI
            )

            Spacer(modifier = Modifier.height(16.dp))

            // The Juicy Neon GameBoard
            GameBoard(
                board = uiState.board,
                winResult = (uiState.gameState as? GameState.Won)?.result,
                onCellClicked = { index ->
                    val center = cellPositions[index] ?: Offset(500f, 1000f)
                    val markColor = if (uiState.currentPlayer == Player.X) NeonCyan else NeonCoral
                    HapticEngine.tap(context)
                    particleController.emitSparkBurst(center, markColor, count = 44)
                    viewModel.onCellClicked(index)
                },
                onCellPositioned = { index, offset ->
                    cellPositions[index] = offset
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Actions (Reset Scores)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = viewModel::resetScores,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Reset Scores",
                        color = Color.White.copy(alpha = 0.25f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // 60FPS Particle Layer
        ParticleHost(controller = particleController)

        // Celebratory Winner Overlay
        WinnerOverlay(
            gameState = uiState.gameState,
            onPlayAgain = viewModel::playAgain,
            particleController = particleController
        )
    }
}

@Composable
fun GameModeSelector(
    selectedMode: GameMode,
    onModeSelected: (GameMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(12.dp))
            .background(Color(0xFF0D1117))
            .border(1.dp, Color(0x33FFFFFF), CutCornerShape(12.dp))
            .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ModePill(
            label = "👥 2P",
            isSelected = selectedMode == GameMode.TWO_PLAYER,
            onClick = { onModeSelected(GameMode.TWO_PLAYER) },
            modifier = Modifier.weight(1f)
        )
        ModePill(
            label = "🤖 EASY",
            isSelected = selectedMode == GameMode.AI_EASY,
            onClick = { onModeSelected(GameMode.AI_EASY) },
            modifier = Modifier.weight(1f)
        )
        ModePill(
            label = "💀 BOSS",
            isSelected = selectedMode == GameMode.AI_IMPOSSIBLE,
            onClick = { onModeSelected(GameMode.AI_IMPOSSIBLE) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ModePill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pill")
    val selectedGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pillGlow"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) NeonCyan.copy(alpha = 0.18f) else Color.Transparent,
        animationSpec = tween(200), label = "pillBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) NeonCyan else Color(0xFF94A3B8),
        animationSpec = tween(200), label = "pillText"
    )
    val pillScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pillScale"
    )

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = pillScale; scaleY = pillScale }
            .clip(CutCornerShape(8.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = if (isSelected) NeonCyan.copy(alpha = selectedGlow) else Color(0x22FFFFFF),
                shape = CutCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}
