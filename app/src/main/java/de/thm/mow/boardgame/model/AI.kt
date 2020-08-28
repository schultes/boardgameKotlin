package de.thm.mow.boardgame.model

import de.thm.mow.boardgame.model.support.*
import kotlin.math.abs

class AI<P, GL : GameLogic<P>>(val logic: GL) {
    var maxSearchDepth = 2
    fun getNextMove(@argLabel("onBoard") board: Board<P>, @argLabel("forPlayer") player: Player) : Move<P> {
        return getNextMove(board, player, player == Player.white, maxSearchDepth)
    }

    private fun getNextMove(@argLabel("onBoard") board: Board<P>, @argLabel("forPlayer") player: Player, maximizingValue: Boolean, @argLabel("withDepth") depth: Int) : Move<P> {
        val bestMoves = mutableListOf<Move<P>>()
        val allMoves = logic.getMoves(board, player)
        for (i in 0 until allMoves.size) {
            var move = allMoves[i]
            val newBoard = board.clone()
            for (step in move.steps) {
                newBoard.applyChanges(step.effects)
            }

            if (depth > 0) {
                val nextMove = getNextMove(newBoard, player.opponent, !maximizingValue, depth - 1)
                move.value = nextMove.value!!
            } else {
                move.value = logic.evaluateBoard(newBoard, player.opponent)
            }

            if (depth == maxSearchDepth) {
                println("depth: ${depth}, (${move.source}), best value: ${bestMoves.firstOrNull()?.value ?: 0}, size = ${bestMoves.size}, current value: ${move.value!!}")
            }

            if (bestMoves.isEmpty() || abs(bestMoves.first().value!! - move.value!!) < 0.01) {
                bestMoves.add(move)
            } else {
                if ((move.value!! > bestMoves.first().value!!) == maximizingValue) {
                    bestMoves.clear()
                    bestMoves.add(move)
                }
            }
        }

        if (bestMoves.isEmpty()) {
            // return empty dummy move if there is no real move
            bestMoves.add(Move<P>(Coords(0, 0), mutableListOf(Step(Coords(0, 0), mutableListOf<Effect<P>>())), logic.evaluateBoard(board, player)))
        }

        return bestMoves.random()
    }
}
