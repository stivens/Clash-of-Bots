package com.codingame.game.core

import com.codingame.game.Player

data class Robot(
    val owner: Player,
    val health: Int,
) {
    val isAlive: Boolean = health > 0
    val isNotAlive: Boolean = !isAlive
}