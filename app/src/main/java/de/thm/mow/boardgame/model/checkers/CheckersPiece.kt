package de.thm.mow.boardgame.model.checkers

import de.thm.mow.boardgame.model.Player
import de.thm.mow.boardgame.model.support.*

enum class CheckersPiece(val rawValue: String) {
    Empty("  "),
    Invalid("x"),
    WhiteMan("◎"),
    BlackMan("◉"),
    WhiteKing("♕"),
    BlackKing("♛");
    companion object {
        fun getMan(@argLabel("forPlayer") player: Player) : CheckersPiece {
            return if (player == Player.white) WhiteMan else BlackMan
        }

        fun getKing(@argLabel("forPlayer") player: Player) : CheckersPiece {
            return if (player == Player.white) WhiteKing else BlackKing
        }
    }

    override fun toString(): String {
        return rawValue
    }

    fun belongs(@argLabel("toPlayer") player: Player) : Boolean {
        if (((this == WhiteMan) || (this == WhiteKing)) && (player == Player.white)) {
            return true
        }

        if (((this == BlackMan) || (this == BlackKing)) && (player == Player.black)) {
            return true
        }

        return false
    }
}
