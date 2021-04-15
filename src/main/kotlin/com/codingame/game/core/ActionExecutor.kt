package com.codingame.game.core

import com.codingame.game.Config
import com.codingame.game.core.Action.*
import com.codingame.game.util.direction2vector

object ActionExecutor {
    private val orderOfPrecedence = listOf(
        Guard::class           to ::executeGuards,
        Move::class            to ::executeMoves,
        Attack::class          to ::executeAttacks,
        Selfdestruction::class to ::executeSelfdestructions,
    )

    fun execute(actions: List<RobotAction>, arena: Arena) {
        val groupedActions = actions.groupBy { it.action::class }

        disableAllGuards(arena)

        orderOfPrecedence.forEach { (actionClass, handler) ->
            handler( groupedActions[actionClass].orEmpty(), arena )
        }
    }

    private fun executeGuards(actions: List<RobotAction>, arena: Arena) {
        actions.forEach { (robot, action) -> require(action is Guard)
            robot.guardUp = true
        }
    }

    private fun executeMoves(actions: List<RobotAction>, arena: Arena) {
        val targetPositions =
            actions.groupBy { (robot, action) -> require(action is Move)

                val currentPosition = arena.getPositionOf(robot)
                require(currentPosition != null)

                val targetPosition = currentPosition.apply(direction2vector(action.direction))
                targetPosition.normalizeOverflow(width = arena.width, height = arena.height)

            }.mapValues { it.value.map { ra -> ra.robot } }

        targetPositions.forEach { (position, robots) ->
            if (robots.size == 1) {
                val robot = robots.single()
                arena.emplace(robot, position)
            } else {
                robots.forEach { damageRobot(it, Config.Robots.COLLISION_DAMAGE) }
            }
        }
    }

    private fun executeAttacks(actions: List<RobotAction>, arena: Arena) {
        actions.forEach { (robot, action) -> require(action is Attack)
            val robotPosition = arena.getPositionOf(robot)
            require(robotPosition != null)
            val targetPosition = robotPosition.apply(direction2vector(action.direction))

            arena.get(targetPosition)?.let { damageRobot(it, Config.Robots.ATTACK_DAMAGE) }
        }
    }

    private fun executeSelfdestructions(actions: List<RobotAction>, arena: Arena) {
        TODO()
    }

    private fun disableAllGuards(arena: Arena) {
        arena.getAllRobots().forEach { (robot, _) -> robot.guardUp = false }
    }

    private fun damageRobot(robot: Robot, damage: Int) {
        val actualDamage = when (robot.guardUp) {
            true -> (damage * Config.Robots.GUARD_MODIFIER).toInt()
            false -> damage
        }

        robot.health -= actualDamage
    }
}