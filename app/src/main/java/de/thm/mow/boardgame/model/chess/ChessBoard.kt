package de.thm.mow.boardgame.model.chess

import de.thm.mow.boardgame.model.*
import de.thm.mow.boardgame.model.support.*

class ChessBoard(pieces: MutableList<ChessPiece>, var evaluation: Double = 0.0, var whiteKing: Coords = Coords(4, 7), var blackKing: Coords = Coords(4, 0)) : Board<ChessPiece>(ChessPiece.Invalid, pieces) {

    constructor() : this(MutableList<ChessPiece>(ChessPiece.Empty, 64))

    override fun clone(): Board<ChessPiece> {
        return ChessBoard(pieces.copy(), evaluation, whiteKing, blackKing)
    }

    override fun applyChanges(changes: MutableList<Effect<ChessPiece>>) {
        val lastChange = changes.last()
        val p = lastChange.newPiece.player!!
        val oldPiece = this[lastChange.coords]
        if (oldPiece.belongs(p.opponent)) {
            evaluation -= oldPiece.value
        }
        if (lastChange.newPiece == ChessPiece.queen(p) && this[changes.first().coords] == ChessPiece.pawn(p)) {
            evaluation += ChessPiece.queen(p).value - ChessPiece.pawn(p).value // promotion
        }
        if (lastChange.newPiece == ChessPiece.king(p)) {
            if (p == Player.white) whiteKing = lastChange.coords else blackKing = lastChange.coords
        }
        super.applyChanges(changes)
    }
}