package de.thm.mow.boardgame.model

import de.thm.mow.boardgame.model.checkers.CheckersPiece
import org.junit.Assert
import org.junit.Test

class CheckersPieceTest {
    @Test
    fun checkersPieceTest() {
        Assert.assertTrue(CheckersPiece.WhiteMan == CheckersPiece.getMan(Player.white))
        Assert.assertTrue(CheckersPiece.BlackKing == CheckersPiece.getKing(Player.black))
        Assert.assertTrue(CheckersPiece.WhiteKing.belongs(Player.white))
        Assert.assertTrue(CheckersPiece.BlackMan.belongs(Player.black))
        Assert.assertFalse(CheckersPiece.BlackMan.belongs(Player.white))
        Assert.assertTrue(CheckersPiece.WhiteKing.toString() == "♕")
    }
}