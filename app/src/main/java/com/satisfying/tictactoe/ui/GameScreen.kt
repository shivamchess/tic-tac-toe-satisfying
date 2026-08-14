package com.satisfying.tictactoe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.satisfying.tictactoe.GameState
import com.satisfying.tictactoe.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header & Score
            ScoreBoard(
                scoreX = uiState.scoreX,
                scoreO = uiState.scoreO,
                currentPlayer = uiState.currentPlayer
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Main Board
            GameBoard(
                board = uiState.board,
                winResult = (uiState.gameState as? GameState.Won)?.result,
                onCellClicked = viewModel::onCellClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Reset Scores Button
            TextButton(onClick = viewModel::resetScores) {
                Text(
                    text = "Reset Scores",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Overlay for Win/Draw
        WinnerOverlay(
            gameState = uiState.gameState,
            onPlayAgain = viewModel::playAgain
        )
    }
}
