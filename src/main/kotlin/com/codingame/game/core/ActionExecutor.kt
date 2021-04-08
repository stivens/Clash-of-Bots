package com.codingame.game.core

import com.codingame.game.core.Action.*

object ActionExecutor {
    private val orderOfPrecedence = listOf(
        Guard::class           to ::executeGuards,
        Move::class            to ::executeMoves,
        Attack::class          to ::executeAttacks,
        Selfdestruction::class to ::executeSelfdestructions,
    )

    fun execute(actions: List<RobotAction>, arena: Arena) {
        val groupedActions = actions.groupBy { it.action::class }

        orderOfPrecedence.forEach { (actionClass, handler) ->
            handler( groupedActions[actionClass].orEmpty(), arena )
        }
    }

    private fun executeGuards(actions: List<RobotAction>, arena: Arena) {
        actions.forEach { (robot, action) ->
            require(action is Guard)
            robot.guardUp = true
        }
    }

    private fun executeMoves(actions: List<RobotAction>, arena: Arena) {
        TODO()
    }

    private fun executeAttacks(actions: List<RobotAction>, arena: Arena) {
        TODO()
    }

    private fun executeSelfdestructions(actions: List<RobotAction>, arena: Arena) {
        TODO()
    }
}