package com.codingame.game.core

import com.codingame.game.Player
import java.util.*

data class Robot(
    val owner: Player,
    val health: Int,
    val uuid: UUID = UUID.randomUUID()
) {
    val isAlive: Boolean = health > 0
    val isNotAlive: Boolean = !isAlive
}