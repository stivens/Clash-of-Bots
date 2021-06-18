package com.codingame.game

import com.codingame.game.core.Action.*
import com.codingame.game.core.Position

object Config {
    object Robots {
        const val MAX_HEALTH       = 10
        const val ATTACK_DAMAGE    = 2
        const val COLLISION_DAMAGE = 1
        const val EXPLOSION_DAMAGE = 4
        const val EXPLOSION_RANGE  = 1
        const val GUARD_MODIFIER   = 0.5
        const val VISION_RANGE     = 1
    }

    object Arena {
        const val WIDTH  = 16
        const val HEIGHT = 16
    }

    object Referee {
        const val MAX_TURNS           = 100
        const val FIRST_TURN_MAX_TIME = 1000
        const val TURN_MAX_TIME       = 50
        const val FRAME_DURATION      = 500

        val SPAWN_SYMMETRY_CENTER        = Position(0, 0)
        const val SPAWN_TURN_DELAY       = 7
        const val SPAWN_ATTEMPTS_LIMIT   = 15
        const val SPAWN_COMPLEMENT_RANGE = 1
    }

    object Interpreter {
        val ACTION_PRIORITY = mapOf(
            Guard::class           to 0,
            Move::class            to 1,
            Attack::class          to 2,
            Selfdestruction::class to 3,
        )
    }

    object Presenter {
        const val FLOOR_SPRITE  = "brick4.png"
        const val SHIELD_SPRITE = "shield.png"
        const val FIST_SPRITE   = "fist.png"
        const val FRAME_SPRITE  = "frame.png"

        const val COLOR_BLUE = 0x1a75ff
        const val COLOR_RED  = 0xff3333
    }
}
