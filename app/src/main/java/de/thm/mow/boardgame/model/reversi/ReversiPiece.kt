package de.thm.mow.boardgame.model.reversi

import de.thm.mow.boardgame.model.Player
import de.thm.mow.boardgame.model.support.*

enum class ReversiPiece(val rawValue: String) {
    Empty("  "),
    Invalid("x"),
    White("◎"),
    Black("◉");
    companion object {
        fun getPiece(@argLabel("forPlayer") player: Player) : ReversiPiece {
            return if (player == Player.white) White else Black
        }
    }

    override fun toString(): String {
        return rawValue
    }

    fun belongs(@argLabel("toPlayer") player: Player) : Boolean {
        if (((this == White) && (player == Player.white))) {
            return true
        }

        if (((this == Black) && (player == Player.black))) {
            return true
        }

        return false
    }
}
