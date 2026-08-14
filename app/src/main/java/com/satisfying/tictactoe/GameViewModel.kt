package com.satisfying.tictactoe

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val winningLines = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Cols
        listOf(0, 4, 8), listOf(2, 4, 6)                   // Diagonals
    )

    fun onCellClicked(index: Int) {
        val currentState = _uiState.value
        
        // Cannot play if game is over or cell is already taken
        if (currentState.gameState !is GameState.Playing || currentState.board[index] != null) {
            return
        }

        val newBoard = currentState.board.toMutableList()
        newBoard[index] = currentState.currentPlayer

        val winResult = checkWin(newBoard, currentState.currentPlayer)
        val isDraw = !newBoard.contains(null) && winResult == null

        val newGameState = when {
            winResult != null -> GameState.Won(winResult)
            isDraw -> GameState.Draw
            else -> GameState.Playing
        }

        val nextPlayer = if (newGameState is GameState.Playing) {
            if (currentState.currentPlayer == Player.X) Player.O else Player.X
        } else currentState.currentPlayer // Keep last player if game ends

        _uiState.update { state ->
            state.copy(
                board = newBoard.toList(),
                currentPlayer = nextPlayer,
                gameState = newGameState,
                scoreX = state.scoreX + if (winResult?.winner == Player.X) 1 else 0,
                scoreO = state.scoreO + if (winResult?.winner == Player.O) 1 else 0
            )
        }
    }

    private fun checkWin(board: List<Player?>, player: Player): WinResult? {
        for (line in winningLines) {
            if (board[line[0]] == player && board[line[1]] == player && board[line[2]] == player) {
                return WinResult(player, line)
            }
        }
        return null
    }

    fun playAgain() {
        _uiState.update { state ->
            state.copy(
                board = List(9) { null },
                gameState = GameState.Playing,
                currentPlayer = if ((state.scoreX + state.scoreO) % 2 == 0) Player.X else Player.O // Alternate starting player based on total games
            )
        }
    }
    
    fun resetScores() {
        _uiState.update { state ->
             state.copy(
                 scoreX = 0,
                 scoreO = 0,
                 board = List(9) { null },
                 gameState = GameState.Playing,
                 currentPlayer = Player.X
             )
        }
    }
}
