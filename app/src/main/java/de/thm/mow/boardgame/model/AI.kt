package de.thm.mow.boardgame.model

class AI<P, GL : GameLogic<P>>(val logic: GL) {
    var maxSearchDepth = 2
    fun getNextMove(@argLabel("onBoard") board: Board<P>, @argLabel("forPlayer") player: Player) : Move<P> {
        return getNextMove(board, player, player == Player.white, maxSearchDepth)
    }

    private fun getNextMove(@argLabel("onBoard") board: Board<P>, @argLabel("forPlayer") player: Player, maximizingValue: Boolean, @argLabel("withDepth") depth: Int) : Move<P> {
        var bestMove: Move<P>? = null
        val allMoves = logic.getMoves(board, player)
        for (i in 0 until allMoves.size) {
            var move = allMoves[i]
            val newBoard = Board<P>(board)
            for (step in move.steps) {
                newBoard.applyChanges(step.effects)
            }

            if (depth > 0) {
                val nextMove = getNextMove(newBoard, player.opponent, !maximizingValue, depth - 1)
                move.value = nextMove.value!!
            } else {
                move.value = logic.evaluateBoard(newBoard)
            }

            if (depth == maxSearchDepth) {
                println("depth: ${depth}, (${move.source}), best value: ${bestMove?.value ?: 0}, current value: ${move.value!!}")
            }

            if ((bestMove == null) || ((move.value!! > bestMove!!.value!!) == maximizingValue)) {
                bestMove = move
            }
        }

        if ((bestMove == null)) {
            // return empty dummy move if there is no real move
            bestMove = Move<P>(Coords(0, 0), mutableListOf(Step(Coords(0, 0), mutableListOf<Effect<P>>())), logic.evaluateBoard(board))
        }

        assert(bestMove!!.value != null)
        return bestMove!!
    }
}