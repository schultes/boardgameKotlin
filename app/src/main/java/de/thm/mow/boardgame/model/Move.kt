package de.thm.mow.boardgame.model

@tuple data class Coords(var x: Int, var y: Int) {}

@tuple data class Effect<P>(var coords: Coords, var newPiece: P) {}

@tuple data class Step<P>(var target: Coords, var effects: MutableList<Effect<P>>) {}

data class Move<P>(val source: Coords, val steps: MutableList<Step<P>>, var value: Double?) {
}