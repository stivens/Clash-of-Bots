package com.codingame.game.core

import com.codingame.game.Config
import com.codingame.game.Player
import com.codingame.game.core.Action.*
import com.codingame.game.core.Action.Direction.*
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

class ActionExecutorTest : ShouldSpec({
    context("execute") {
        should("properly move robots") {
            val arena = Arena(width = 12, height = 12)

            val robot1 = Robot(owner = Player(), health = 100)
            val robot2 = Robot(owner = Player(), health = 100)

            val actions = listOf(
                RobotAction(robot1, Move(UP)),
                RobotAction(robot2, Move(RIGHT)),
            )

            val pos1 = Position(x = 5, y = 5)
            val pos2 = Position(x = 9, y = 9)

            arena.emplace(robot1, pos1)
            arena.emplace(robot2, pos2)

            ActionExecutor.execute(actions, arena)

            arena.getPositionOf(robot1) shouldBe Position(pos1.x, pos1.y - 1)
            arena.getPositionOf(robot2) shouldBe Position(pos2.x + 1, pos2.y)

            robot1.health shouldBe 100
            robot2.health shouldBe 100
        }

        should("properly apply attacks") {
            val arena = Arena(width = 12, height = 12)

            val robot1 = Robot(owner = Player(), health = 100)
            val robot2 = Robot(owner = Player(), health = 100)

            val pos1 = Position(x = 0, y = 0)
            val pos2 = Position(x = 1, y = 0)

            arena.emplace(robot1, pos1)
            arena.emplace(robot2, pos2)

            val actions = listOf(
                RobotAction(robot1, Attack(RIGHT)),
                RobotAction(robot2, Attack(LEFT)),
            )

            ActionExecutor.execute(actions, arena)

            robot1.health shouldBe 100 - Config.Robots.ATTACK_DAMAGE
            robot2.health shouldBe 100 - Config.Robots.ATTACK_DAMAGE
        }

        should("detect and properly handle collisions when two robots want to enter same field") {
            val arena = Arena(width = 12, height = 12)

            val robot1 = Robot(owner = Player(), health = 100)
            val robot2 = Robot(owner = Player(), health = 100)

            val actions = listOf(
                RobotAction(robot1, Move(RIGHT)),
                RobotAction(robot2, Move(LEFT)),
            )

            val pos1 = Position(x = 5, y = 0)
            val pos2 = Position(x = 7, y = 0)

            arena.emplace(robot1, pos1)
            arena.emplace(robot2, pos2)

            ActionExecutor.execute(actions, arena)

            arena.getPositionOf(robot1) shouldBe pos1
            arena.getPositionOf(robot2) shouldBe pos2

            robot1.health shouldBe 100 - Config.Robots.COLLISION_DAMAGE
            robot2.health shouldBe 100 - Config.Robots.COLLISION_DAMAGE
        }
    }

    should("properly handle guards") {
        val arena = Arena(width = 12, height = 12)

        val robot = Robot(owner = Player())
        arena.emplace(robot, Position(0, 0))

        val activateGuard = listOf(RobotAction(robot, Guard()))

        robot.guardUp shouldBe false
        ActionExecutor.execute(activateGuard, arena)
        robot.guardUp shouldBe true

        val someOtherAction = listOf(RobotAction(robot, Move(UP)))

        ActionExecutor.execute(someOtherAction, arena)
        robot.guardUp shouldBe false
    }

    should("execute guards before attacks") {
        val arena = Arena(width = 12, height = 12)

        val robotGuard = Robot(owner = Player(), health = 100)
        val robotAttack = Robot(owner = Player())

        val posGuard = Position(x = 0, y = 0)
        val posAttack = Position(x = 1, y = 0)

        arena.emplace(robotGuard, posGuard)
        arena.emplace(robotAttack, posAttack)

        val actions = listOf(
            RobotAction(robotGuard, Guard()),
            RobotAction(robotAttack, Attack(LEFT)),
        ).shuffled()

        ActionExecutor.execute(actions, arena)

        robotGuard.health shouldBeGreaterThan 100 - Config.Robots.ATTACK_DAMAGE
    }

    should("properly execute selfdestructions") {
        val arena = Arena(width = 12, height = 12)

        val aboutToExplode = Robot(owner = Player())

        arena.emplace(aboutToExplode, Position(0, 0))

        val targets = listOf(
            Pair( Robot(owner = Player(), health = 100), Position(1, 0) ),
            Pair( Robot(owner = Player(), health = 100), Position(0, 1) ),
            Pair( Robot(owner = Player(), health = 100), Position(0, 2) ),
            Pair( Robot(owner = Player(), health = 100), Position(1, 1) ),
            Pair( Robot(owner = Player(), health = 100), Position(2, 2) ),
            Pair( Robot(owner = Player(), health = 100), Position(9, 9) ),
            Pair( Robot(owner = Player(), health = 100), Position(5, 5) ),
        ).onEach { (robot, position) -> arena.emplace(robot, position) }

        val explosionRange = arena.getPositionOf(aboutToExplode)!!
            .allNeighborsInRange(Config.Robots.EXPLOSION_RANGE).flatten()

        val inRange = targets.filter { (_, position) -> position in explosionRange }
        val notInRange = targets.filter { (_, position) -> position !in explosionRange }

        require(inRange.isNotEmpty())
        require(notInRange.isNotEmpty())

        val actions = listOf(
            RobotAction(aboutToExplode, Selfdestruction())
        )

        ActionExecutor.execute(actions, arena)

        inRange.forAll { (robot, _) ->
            robot.health shouldBe 100 - Config.Robots.EXPLOSION_DAMAGE
        }

        notInRange.forAll { (robot, _) ->
            robot.health shouldBe 100
        }

        aboutToExplode.isNotAlive shouldBe true
        arena.getPositionOf(aboutToExplode) shouldBe null
    }

    should("execute attacks before selfdestructions") {
        val arena = Arena(width = 12, height = 12)

        val aboutToExplode = Robot(owner = Player(), health = Config.Robots.ATTACK_DAMAGE)
        val robotAttack = Robot(owner = Player(), health = 100)

        arena.emplace(aboutToExplode, Position(0, 0))
        arena.emplace(robotAttack, Position(x = 0, y = 1))

        val actions = listOf(
            RobotAction(aboutToExplode, Selfdestruction()),
            RobotAction(robotAttack, Attack(UP))
        ).shuffled()

        ActionExecutor.execute(actions, arena)

        robotAttack.health shouldBe 100 // suicide was killed so explosion is canceled
    }

    should("execute moves before attacks") {
        val arena = Arena(width = 12, height = 12)

        val aboutToRunAway = Robot(owner = Player(), health = 100)
        val robotAttack = Robot(owner = Player(), health = 100)

        arena.emplace(aboutToRunAway, Position(0, 0))
        arena.emplace(robotAttack, Position(x = 0, y = 1))

        val actions = listOf(
            RobotAction(aboutToRunAway, Move(UP)),
            RobotAction(robotAttack, Attack(UP))
        ).shuffled()

        ActionExecutor.execute(actions, arena)

        aboutToRunAway.health shouldBe 100
    }
})