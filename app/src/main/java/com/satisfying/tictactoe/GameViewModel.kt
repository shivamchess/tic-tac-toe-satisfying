package com.satisfying.tictactoe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satisfying.tictactoe.audio.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

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

        // Cannot move if not playing, cell occupied, or AI is currently thinking
        if (currentState.gameState !is GameState.Playing ||
            currentState.board[index] != null ||
            currentState.isThinkingAI
        ) {
            return
        }

        makeMove(index, currentState.currentPlayer)
    }

    private fun makeMove(index: Int, player: Player) {
        val currentState = _uiState.value
        val newBoard = currentState.board.toMutableList()
        newBoard[index] = player

        val newMoveCount = currentState.moveCount + 1
        SoundManager.playPop(newMoveCount)

        val winResult = checkWin(newBoard, player)
        val isDraw = !newBoard.contains(null) && winResult == null

        val newGameState = when {
            winResult != null -> GameState.Won(winResult)
            isDraw -> GameState.Draw
            else -> GameState.Playing
        }

        // Trigger sounds on game over
        if (winResult != null) {
            SoundManager.playWin()
        } else if (isDraw) {
            SoundManager.playDraw()
        }

        val nextPlayer = if (newGameState is GameState.Playing) {
            if (player == Player.X) Player.O else Player.X
        } else player

        // Calculate score & streak
        val isXWin = winResult?.winner == Player.X
        val isOWin = winResult?.winner == Player.O
        val newStreak = when {
            isXWin -> currentState.streak + 1
            isOWin -> 0
            else -> currentState.streak
        }

        _uiState.update { state ->
            state.copy(
                board = newBoard.toList(),
                currentPlayer = nextPlayer,
                gameState = newGameState,
                scoreX = state.scoreX + if (isXWin) 1 else 0,
                scoreO = state.scoreO + if (isOWin) 1 else 0,
                streak = newStreak,
                moveCount = newMoveCount
            )
        }

        // Trigger AI turn if needed
        if (newGameState is GameState.Playing &&
            nextPlayer == Player.O &&
            currentState.gameMode != GameMode.TWO_PLAYER
        ) {
            triggerAiMove(newBoard, currentState.gameMode)
        }
    }

    private fun triggerAiMove(board: List<Player?>, mode: GameMode) {
        _uiState.update { it.copy(isThinkingAI = true) }
        viewModelScope.launch {
            // Human-like thinking delay for satisfying pacing
            delay(350)

            val bestIndex = if (mode == GameMode.AI_EASY) {
                // Easy mode: 70% random, 30% smart
                if (Random.nextFloat() < 0.7f) {
                    getAvailableMoves(board).randomOrNull() ?: -1
                } else {
                    findBestMoveMinimax(board, Player.O)
                }
            } else {
                // Impossible mode: Unbeatable Minimax
                findBestMoveMinimax(board, Player.O)
            }

            _uiState.update { it.copy(isThinkingAI = false) }
            if (bestIndex in 0..8 && _uiState.value.gameState is GameState.Playing) {
                makeMove(bestIndex, Player.O)
            }
        }
    }

    private fun getAvailableMoves(board: List<Player?>): List<Int> {
        return board.indices.filter { board[it] == null }
    }

    private fun findBestMoveMinimax(board: List<Player?>, aiPlayer: Player): Int {
        val availableMoves = getAvailableMoves(board)
        if (availableMoves.isEmpty()) return -1

        // If board is empty, picking a corner or center is best and fast
        if (availableMoves.size == 9) return listOf(0, 2, 4, 6, 8).random()

        var bestScore = Int.MIN_VALUE
        var bestMove = availableMoves.first()

        for (move in availableMoves) {
            val nextBoard = board.toMutableList()
            nextBoard[move] = aiPlayer
            val score = minimax(nextBoard, 0, false, aiPlayer)
            if (score > bestScore) {
                bestScore = score
                bestMove = move
            }
        }
        return bestMove
    }

    private fun minimax(board: List<Player?>, depth: Int, isMaximizing: Boolean, aiPlayer: Player): Int {
        val opponent = if (aiPlayer == Player.O) Player.X else Player.O
        val winResultO = checkWin(board, aiPlayer)
        if (winResultO != null) return 10 - depth

        val winResultX = checkWin(board, opponent)
        if (winResultX != null) return depth - 10

        val availableMoves = getAvailableMoves(board)
        if (availableMoves.isEmpty()) return 0

        return if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (move in availableMoves) {
                val nextBoard = board.toMutableList()
                nextBoard[move] = aiPlayer
                val evaluation = minimax(nextBoard, depth + 1, false, aiPlayer)
                maxEval = maxOf(maxEval, evaluation)
            }
            maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for (move in availableMoves) {
                val nextBoard = board.toMutableList()
                nextBoard[move] = opponent
                val evaluation = minimax(nextBoard, depth + 1, true, aiPlayer)
                minEval = minOf(minEval, evaluation)
            }
            minEval
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

    fun setGameMode(mode: GameMode) {
        SoundManager.playClick()
        _uiState.update { state ->
            state.copy(
                gameMode = mode,
                board = List(9) { null },
                gameState = GameState.Playing,
                currentPlayer = Player.X,
                moveCount = 0
            )
        }
    }

    fun playAgain() {
        SoundManager.playClick()
        _uiState.update { state ->
            state.copy(
                board = List(9) { null },
                gameState = GameState.Playing,
                currentPlayer = Player.X,
                moveCount = 0,
                isThinkingAI = false
            )
        }
    }

    fun resetScores() {
        SoundManager.playClick()
        _uiState.update { state ->
            state.copy(
                scoreX = 0,
                scoreO = 0,
                streak = 0,
                board = List(9) { null },
                gameState = GameState.Playing,
                currentPlayer = Player.X,
                moveCount = 0,
                isThinkingAI = false
            )
        }
    }
}
