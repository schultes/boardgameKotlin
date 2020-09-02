package de.thm.mow.boardgame.model

import de.thm.mow.boardgame.model.support.*

open class Board<P>(val invalid: P, val pieces: MutableList<P>) {
    val columns = 8
    val rows = 8
    constructor(empty: P, invalid: P) : this(invalid, MutableList<P>(empty, 64))
    protected open fun clone() : Board<P> {
        return Board<P>(invalid, pieces.copy())
    }

    fun changedCopy(changes: MutableList<Effect<P>>) : Board<P> {
        val copiedBoard = clone()
        copiedBoard.applyChanges(changes)
        return copiedBoard
    }

    fun changedCopy(move: Move<P>) : Board<P> {
        val copiedBoard = clone()
        copiedBoard.applyChanges(move)
        return copiedBoard
    }

    private fun indexIsValidFor(row: Int, column: Int) : Boolean {
        return row >= 0 && row < rows && column >= 0 && column < columns
    }

    private fun indexFor(row: Int, column: Int) : Int {
        return (row * columns) + column
    }

    operator fun get(coords: Coords) : P {
        return this[coords.x, coords.y]
    }

    operator fun set(coords: Coords, newValue: P) {
        this[coords.x, coords.y] = newValue
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

    fun applyChanges(move: Move<P>) {
        move.steps.forEach {
            applyChanges(it.effects)
        }
    }

    open fun applyChanges(@argLabel("_") changes: MutableList<Effect<P>>) {
        for (change in changes) {
            this[change.coords] = change.newPiece
        }
    }
}
