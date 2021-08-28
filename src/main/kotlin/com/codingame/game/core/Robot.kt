package com.codingame.game.core

import com.codingame.game.Config
import com.codingame.game.Player
import java.util.*

class Robot(
    val owner: Player,
    var health: Int = Config.Robots.MAX_HEALTH,
    var guardUp: Boolean = false,
    val uuid: UUID = UUID.randomUUID()
) {
    val isAlive: Boolean get() = health > 0
    val isNotAlive: Boolean get() = !isAlive

    override fun hashCode() = uuid.hashCode()
    override fun equals(other: Any?) =
        other is Robot && other.uuid == this.uuid
}