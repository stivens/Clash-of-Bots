package com.codingame.game

import com.codingame.game.core.Action.*
import com.codingame.game.core.Arena
import com.codingame.game.core.Position
import com.codingame.game.core.Robot
import com.codingame.gameengine.module.entities.GraphicEntityModule

class Presenter(private val arena: Arena, private val graphicEntityModule: GraphicEntityModule) {
    init {
        drawArena()
    }

    private fun drawArena() {
//        TODO()
    }

    fun triggerGuard(robot: Robot, guard: Guard) {
//        TODO()
    }

    fun triggerGuardDisable(robot: Robot) {
//        TODO()
    }

    fun triggerMove(robot: Robot, move: Move) {
//        TODO()
    }

    fun triggerCollision(robot: Robot, move: Move) {
//        TODO()
    }

    fun triggerAttack(robot: Robot, attack: Attack) {
//        TODO()
    }

    fun triggerSelfdestruction(robot: Robot, selfdestruction: Selfdestruction) {
//        TODO()
    }

    fun triggerDamage(robot: Robot) {
//        TODO()
    }

    fun triggerDeath(robot: Robot) {
//        TODO()
    }
}