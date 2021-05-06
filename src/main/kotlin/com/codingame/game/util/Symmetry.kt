package com.codingame.game.util

import com.codingame.game.core.Position

object Symmetry {
    fun centerSymmetry(center: Position, a: Position): Position =
        Position(x = 2 * center.x - a.x, y = 2 * center.y - a.y)
}