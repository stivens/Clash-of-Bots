package com.codingame.game.core

import com.codingame.game.Config
import com.codingame.game.Presenter
import com.codingame.game.core.Action.*
import com.codingame.game.util.MoveGraph
import com.codingame.game.util.asVector

class Interpreter(private val arena: Arena, private val presenter: Presenter?) {
    private val orderOfPrecedence = listOf(Guard::class, Move::class, Attack::class, Selfdestruction::class)
        .sortedBy { Config.Interpreter.ACTION_PRIORITY[it] }

    fun execute(robotActions: Map<Robot, Action>) {
        require(
            robotActions.keys == arena.getAllRobots().map { (robot, _) -> robot}.toSet()
        ) { "There must be a action for every single robot." }

        disableAllGuards()

        for (actionClass in orderOfPrecedence) {
            when (actionClass) {
                Guard::class -> executeGuards(
                    robotActions.filter { (_, action) -> action::class == Guard::class }
                        .let(::discardCanceledActions) as Map<Robot, Guard>
                )
                Move::class -> executeMoves(
                    robotActions
                        .let(::discardCanceledActions)
                )
                Attack::class -> executeAttacks(
                    robotActions.filter { (_, action) -> action::class == Attack::class }
                        .let(::discardCanceledActions) as Map<Robot, Attack>
                )
                Selfdestruction::class -> executeSelfdestructions(
                    robotActions.filter { (_, action) -> action::class == Selfdestruction::class }
                        .let(::discardCanceledActions) as Map<Robot, Selfdestruction>
                )
            }

            clearDeadRobots()
        }
    }

    private fun executeGuards(robotActions: Map<Robot, Guard>) {
        robotActions.forEach { (robot, guard) ->
            robot.guardUp = true
            presenter?.triggerGuard(robot, guard)
        }
    }

    private fun executeMoves(robotActions: Map<Robot, Action>) {
        fun getDepartureAndDestination(robot: Robot, move: Move): Pair<Position, Position> {
            val departure = arena.getPositionOf(robot)
            require(departure != null)

            val destination = departure.apply(move.direction.asVector())
                .normalizeOverflow(arena.width, arena.height)

            return Pair(departure, destination)
        }

        val moveGraph = MoveGraph()

        robotActions.forEach { (robot, action) ->
            when (action) {
                is Move -> {
                    val (departure, destination) = getDepartureAndDestination(robot, action)
                    moveGraph.registerMoveAttempt(from = departure, to = destination)
                }
                else -> moveGraph.registerPositionOccupancy(arena.getPositionOf(robot)!!)
            }
        }

        moveGraph.resolve()

        val moves = robotActions.filter { (_, action) -> action::class == Move::class } as Map<Robot, Move>

        moves.forEach { (robot, move) ->
            val (departure, destination) = getDepartureAndDestination(robot, move)

            if (moveGraph.checkCollision(departure, destination)) {
                listOfNotNull(robot, arena.get(destination)).forEach { r ->
                    damageRobot(r, Config.Robots.COLLISION_DAMAGE)
                }
                presenter?.triggerCollision(robot, move)
            } else {
                arena.emplace(robot, destination)
                presenter?.triggerMove(robot, move)
            }
        }
    }

    private fun executeAttacks(robotActions: Map<Robot, Attack>) {
        robotActions.forEach { (robot, attack) ->
            val robotPosition = arena.getPositionOf(robot)
            require(robotPosition != null)
            val targetPosition = robotPosition.apply(attack.direction.asVector())

            arena.get(targetPosition)?.let {damageRobot(it, Config.Robots.ATTACK_DAMAGE) }
            presenter?.triggerAttack(robot, attack)
        }
    }

    private fun executeSelfdestructions(robotActions: Map<Robot, Selfdestruction>) {
        robotActions.forEach { (robot, selfdestruction) ->

            val position = arena.getPositionOf(robot)
            require(position != null)

            val affectedRobots = position
                .allNeighborsInRange(Config.Robots.EXPLOSION_RANGE).flatten()
                .mapNotNull { arena.get(it) }

            affectedRobots.forEach { damageRobot(it, Config.Robots.EXPLOSION_DAMAGE) }

            robot.health = -1
            presenter?.triggerSelfdestruction(robot, selfdestruction)
        }
    }

    private fun disableAllGuards() {
        arena.getAllRobots().forEach {
            (robot, _) -> robot.guardUp = false
            presenter?.triggerGuardDisable(robot)
        }
    }

    private fun damageRobot(robot: Robot, damage: Int) {
        val actualDamage = when (robot.guardUp) {
            true -> (damage * Config.Robots.GUARD_MODIFIER).toInt()
            false -> damage
        }

        robot.health -= actualDamage
        presenter?.triggerDamage(robot)
    }

    private fun clearDeadRobots() {
        arena.getAllRobots()
            .map { (robot, _) -> robot }
            .filter { it.isNotAlive }
            .forEach {
                arena.remove(it)
                presenter?.triggerDeath(it)
            }
    }

    private fun discardCanceledActions(robotActions: Map<Robot, Action>): Map<Robot, Action> =
        robotActions.filter { (robot, _) -> robot.isAlive }
}