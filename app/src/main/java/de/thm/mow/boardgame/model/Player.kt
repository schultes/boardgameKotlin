package de.thm.mow.boardgame.model

enum class Player {
    white, black;
    val opponent: Player
        get() = if (this == white) black else white

    val sign: Int
        get() = if (this == white) +1 else -1

}
