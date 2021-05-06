package com.codingame.game

import com.codingame.game.core.Position

object Config {
    object Robots {
        const val MAX_HEALTH: Int = 10
        const val ATTACK_DAMAGE: Int = 2
        const val COLLISION_DAMAGE: Int = 1
        const val EXPLOSION_DAMAGE: Int = 4
        const val EXPLOSION_RANGE: Int = 1
        const val GUARD_MODIFIER: Double = 0.5
        const val VISION_RANGE: Int = 1
    }

    object Referee {
        val SPAWN_SYMMETRY_CENTER = Position(0, 0)
        const val SPAWN_COMPLEMENT_RANGE = 1
    }
}
