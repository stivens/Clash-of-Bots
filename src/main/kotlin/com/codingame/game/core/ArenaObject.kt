package com.codingame.game.core

import com.codingame.game.Player

sealed class ArenaObject

object VOID  : ArenaObject()
object EMPTY : ArenaObject()
object WALL  : ArenaObject()
object ENTRY : ArenaObject()

data class Robot(
    val owner: Player,
    val id: Int,
    val position: Position,
    val health: Int,
) : ArenaObject()
{
    val isAlive: Boolean = health > 0
    val isNotAlive: Boolean = !isAlive
}
