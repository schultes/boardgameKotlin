package de.thm.mow.boardgame.model

import de.thm.mow.boardgame.model.checkers.CheckersPiece
import org.junit.Assert
import org.junit.Test

class MoveTest {
    @Test
    fun moveTest() {
        val move = Move<CheckersPiece>(Coords(2,3), mutableListOf(Step<CheckersPiece>(Coords(2,4),mutableListOf())), null)
        Assert.assertTrue(move.source.x == 2)
        Assert.assertTrue(move.value == null)
        Assert.assertTrue(move.steps[0].target.y == 4)
        Assert.assertTrue(move.steps[0].effects.size == 0)
    }
}