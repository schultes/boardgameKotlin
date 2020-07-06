package de.thm.mow.boardgame.model

import de.thm.mow.boardgame.model.support.*

interface Game {
    val isCurrentPlayerWhite: Boolean
    val result: GameResult
    val evaluation: Double
    fun getFieldAsString(@argLabel("atCoords") coords: Coords) : String
    fun getCurrentTargets() : MutableList<Coords>
    fun restart()
    fun userAction(@argLabel("atCoords") coords: Coords) : Boolean
    fun aiMove() : Boolean
    fun aiSetSearchDepth(@argLabel("_") depth: Int)
}

class GenericGame<P, GL : GameLogic<P>>(private val logic: GL) : Game {
    private val ai: AI<P, GL>
    private var currentPlayer = Player.white
    private var currentBoard: Board<P>
    private var currentMoves: MutableList<Move<P>>? = null
    private var currentStepIndex = 0
    init {
        this.ai = AI(logic)
        currentBoard = logic.getInitialBoard()
    }

    override val isCurrentPlayerWhite: Boolean
        get() {
            return currentPlayer == Player.white
        }

    override val result: GameResult
        get() {
            return logic.getResult(currentBoard, currentPlayer)
        }

    override val evaluation: Double
        get() = logic.evaluateBoard(currentBoard)

    override fun getFieldAsString(@argLabel("atCoords") coords: Coords) : String {
        val piece = currentBoard[coords.x, coords.y]
        return piece.toString()
    }

    override fun getCurrentTargets() : MutableList<Coords> {
        val result = mutableListOf<Coords>()
        currentMoves?.let { moves ->
            for (move in moves) {
                val target: Coords = move.steps[currentStepIndex].target
                result.add(target)
            }
        }

        return result
    }

    override fun restart() {
        currentPlayer = Player.white
        currentBoard = logic.getInitialBoard()
        currentMoves = null
    }

    override fun userAction(@argLabel("atCoords") coords: Coords) : Boolean {
        if (result.finished) {
            return false
        }

        val (x, y) = coords
        if ((currentMoves != null)) {
            for (move in currentMoves!!) {
                val steps = move.steps
                if (x == steps[currentStepIndex].target.x && y == steps[currentStepIndex].target.y) {
                    currentBoard.applyChanges(steps[currentStepIndex].effects)
                    if ((currentStepIndex + 1 == steps.size)) {
                        currentMoves = null
                        currentStepIndex = 0
                        currentPlayer = currentPlayer.opponent
                        println(logic.evaluateBoard(currentBoard))
                    } else {
                        val remainingMoves = mutableListOf<Move<P>>()
                        for (m in currentMoves!!) {
                            if (x == m.steps[currentStepIndex].target.x && y == m.steps[currentStepIndex].target.y) {
                                remainingMoves.add(m)
                            }
                        }

                        currentMoves = remainingMoves
                        currentStepIndex++
                    }

                    return true
                }
            }
        } else {
            val moves = logic.getMoves(currentBoard, currentPlayer, coords)
            if ((moves.isEmpty())) {
                val allMoves = logic.getMoves(currentBoard, currentPlayer)
                if (allMoves.isEmpty()) {
                    // add empty dummy move if there is no real move
                    val dummyMove = Move<P>(Coords(x, y), mutableListOf(Step(Coords(x, y), mutableListOf<Effect<P>>())), null)
                    moves.add(dummyMove)
                }
            }

            if ((!moves.isEmpty())) {
                currentMoves = moves
                return true
            }
        }

        return false
    }

    override fun aiMove() : Boolean {
        if (result.finished) {
            return false
        }

        val nextMove = ai.getNextMove(currentBoard, currentPlayer)
        for (step in nextMove.steps) {
            currentBoard.applyChanges(step.effects)
        }

        println(logic.evaluateBoard(currentBoard))
        currentPlayer = currentPlayer.opponent
        return true
    }

    override fun aiSetSearchDepth(@argLabel("_") depth: Int) {
        ai.maxSearchDepth = depth
    }
}
