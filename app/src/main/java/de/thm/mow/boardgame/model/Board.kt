package de.thm.mow.boardgame.model

import de.thm.mow.boardgame.model.support.*

class Board<P> {
    val columns = 8
    val rows = 8
    var pieces: MutableList<P>
    val invalid: P
    constructor(empty: P, invalid: P) {
        pieces = MutableList<P>(empty, columns * rows)
        this.invalid = invalid
    }

    constructor(board: Board<P>) {
        pieces = board.pieces.copy()
        invalid = board.invalid
    }

    private fun indexIsValidFor(row: Int, column: Int) : Boolean {
        return row >= 0 && row < rows && column >= 0 && column < columns
    }

    private fun indexFor(row: Int, column: Int) : Int {
        return (row * columns) + column
    }

    operator fun get(column: Int, row: Int) : P {
        if ((!indexIsValidFor(row, column))) {
            return invalid
        }

        return pieces[indexFor(row, column)]
    }

    operator fun set(column: Int, row: Int, newValue: P) {
        assert(indexIsValidFor(row, column), "Index out of range")
        pieces[indexFor(row, column)] = newValue
    }

    fun applyChanges(@argLabel("_") changes: MutableList<Effect<P>>) {
        for (change in changes) {
            this[change.coords.x, change.coords.y] = change.newPiece
        }
    }
}
