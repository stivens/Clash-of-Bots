package com.codingame.game.core

import com.codingame.game.Config
import com.codingame.game.Player
import com.codingame.game.core.Action.*
import com.codingame.game.core.Action.Direction.*
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe

class InterpreterTest : ShouldSpec({
    context("execute") {
        should("properly move robots") {
            val arena = Arena(width = 12, height = 12)
            val interpreter = Interpreter(arena, presenter = null)

            val robot1 = Robot(owner = Player(), health = 100)
            val robot2 = Robot(owner = Player(), health = 100)

            val actions = listOf(
                Pair(robot1, Move(UP)),
                Pair(robot2, Move(RIGHT)),
            )

            val pos1 = Position(x = 5, y = 5)
            val pos2 = Position(x = 9, y = 9)

            arena.emplace(robot1, pos1)
            arena.emplace(robot2, pos2)

            interpreter.execute(actions)

            arena.getPositionOf(robot1) shouldBe Position(pos1.x, pos1.y - 1)
            arena.getPositionOf(robot2) shouldBe Position(pos2.x + 1, pos2.y)

            robot1.health shouldBe 100
            robot2.health shouldBe 100
        }

        should("properly apply attacks") {
            val arena = Arena(width = 12, height = 12)
            val interpreter = Interpreter(arena, presenter = null)

            val robot1 = Robot(owner = Player(), health = 100)
            val robot2 = Robot(owner = Player(), health = 100)

            val pos1 = Position(x = 0, y = 0)
            val pos2 = Position(x = 1, y = 0)

            arena.emplace(robot1, pos1)
            arena.emplace(robot2, pos2)

            val actions = listOf(
                Pair(robot1, Attack(RIGHT)),
                Pair(robot2, Attack(LEFT)),
            )

            interpreter.execute(actions)

            robot1.health shouldBe 100 - Config.Robots.ATTACK_DAMAGE
            robot2.health shouldBe 100 - Config.Robots.ATTACK_DAMAGE
        }

        should("detect and properly handle collisions when two robots want to enter same field") {
            val arena = Arena(width = 12, height = 12)
            val interpreter = Interpreter(arena, presenter = null)

            val robot1 = Robot(owner = Player(), health = 100)
            val robot2 = Robot(owner = Player(), health = 100)

            val actions = listOf(
                Pair(robot1, Move(RIGHT)),
                Pair(robot2, Move(LEFT)),
            )

            val pos1 = Position(x = 5, y = 0)
            val pos2 = Position(x = 7, y = 0)

            arena.emplace(robot1, pos1)
            arena.emplace(robot2, pos2)

            interpreter.execute(actions)

            arena.getPositionOf(robot1) shouldBe pos1
            arena.getPositionOf(robot2) shouldBe pos2

            robot1.health shouldBe 100 - Config.Robots.COLLISION_DAMAGE
            robot2.health shouldBe 100 - Config.Robots.COLLISION_DAMAGE
        }

        should("detect and properly handle collisions when robot attempts to enter occupied position") {
            val arena = Arena(width = 12, height = 12)
            val interpreter = Interpreter(arena, presenter = null)

            val robot1 = Robot(owner = Player(), health = 100)
            val robot2 = Robot(owner = Player(), health = 100)

            val actions = listOf(
                Pair(robot1, Move(RIGHT)),
                Pair(robot2, Attack(RIGHT)),
            )

            val pos1 = Position(x = 5, y = 0)
            val pos2 = Position(x = 6, y = 0)

            arena.emplace(robot1, pos1)
            arena.emplace(robot2, pos2)

            interpreter.execute(actions)

            arena.getPositionOf(robot1) shouldBe pos1
            arena.getPositionOf(robot2) shouldBe pos2

            robot1.health shouldBe 100 - Config.Robots.COLLISION_DAMAGE
            robot2.health shouldBe 100 - Config.Robots.COLLISION_DAMAGE
        }

        should("detect and properly handle chaining collisions") {
            val arena = Arena(width = 12, height = 12)
            val interpreter = Interpreter(arena, presenter = null)

            val robot1 = Robot(owner = Player(), health = 100)
            val robot2 = Robot(owner = Player(), health = 100)
            val robot3 = Robot(owner = Player(), health = 100)
            val robot4 = Robot(owner = Player(), health = 100)

            val actions = listOf(
                Pair(robot1, Move(RIGHT)),
                Pair(robot2, Move(RIGHT)),
                Pair(robot3, Move(RIGHT)),
                Pair(robot4, Attack(RIGHT)),
            )

            val pos1 = Position(x = 5, y = 0)
            val pos2 = Position(x = 6, y = 0)
            val pos3 = Position(x = 7, y = 0)
            val pos4 = Position(x = 8, y = 0)

            arena.emplace(robot1, pos1)
            arena.emplace(robot2, pos2)
            arena.emplace(robot3, pos3)
            arena.emplace(robot4, pos4)

            interpreter.execute(actions)

            arena.getPositionOf(robot1) shouldBe pos1
            arena.getPositionOf(robot2) shouldBe pos2
            arena.getPositionOf(robot3) shouldBe pos3
            arena.getPositionOf(robot4) shouldBe pos4

            robot1.health shouldBeLessThan 100
            robot2.health shouldBeLessThan 100
            robot3.health shouldBeLessThan 100
            robot4.health shouldBeLessThan 100
        }

        should("properly handle guards") {
            val arena = Arena(width = 12, height = 12)
            val interpreter = Interpreter(arena, presenter = null)

            val robot = Robot(owner = Player())
            arena.emplace(robot, Position(0, 0))

            val activateGuard = listOf(Pair(robot, Guard()))

            robot.guardUp shouldBe false
            interpreter.execute(activateGuard)
            robot.guardUp shouldBe true

            val someOtherAction = listOf(Pair(robot, Move(UP)))

            interpreter.execute(someOtherAction)
            robot.guardUp shouldBe false
        }

        should("execute guards before attacks") {
            val arena = Arena(width = 12, height = 12)
            val interpreter = Interpreter(arena, presenter = null)

            val robotGuard = Robot(owner = Player(), health = 100)
            val robotAttack = Robot(owner = Player())

            val posGuard = Position(x = 0, y = 0)
            val posAttack = Position(x = 1, y = 0)

            arena.emplace(robotGuard, posGuard)
            arena.emplace(robotAttack, posAttack)

            val actions = listOf(
                Pair(robotGuard, Guard()),
                Pair(robotAttack, Attack(LEFT)),
            ).shuffled()

            interpreter.execute(actions)

            robotGuard.health shouldBeGreaterThan 100 - Config.Robots.ATTACK_DAMAGE
        }

        should("properly execute selfdestructions") {
            val arena = Arena(width = 12, height = 12)
            val interpreter = Interpreter(arena, presenter = null)

            val aboutToExplode = Robot(owner = Player())

            arena.emplace(aboutToExplode, Position(0, 0))

            val targets = listOf(
                Pair(Robot(owner = Player(), health = 100), Position(1, 0)),
                Pair(Robot(owner = Player(), health = 100), Position(0, 1)),
                Pair(Robot(owner = Player(), health = 100), Position(0, 2)),
                Pair(Robot(owner = Player(), health = 100), Position(1, 1)),
                Pair(Robot(owner = Player(), health = 100), Position(2, 2)),
                Pair(Robot(owner = Player(), health = 100), Position(9, 9)),
                Pair(Robot(owner = Player(), health = 100), Position(5, 5)),
            ).onEach { (robot, position) -> arena.emplace(robot, position) }

            val explosionRange = arena.getPositionOf(aboutToExplode)!!
                .allNeighborsInRange(Config.Robots.EXPLOSION_RANGE).flatten()

            val inRange = targets.filter { (_, position) -> position in explosionRange }
            val notInRange = targets.filter { (_, position) -> position !in explosionRange }

            require(inRange.isNotEmpty())
            require(notInRange.isNotEmpty())

            val actions = listOf(
                Pair(aboutToExplode, Selfdestruction())
            )

            interpreter.execute(actions)

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
            val interpreter = Interpreter(arena, presenter = null)

            val aboutToExplode = Robot(owner = Player(), health = Config.Robots.ATTACK_DAMAGE)
            val robotAttack = Robot(owner = Player(), health = 100)

            arena.emplace(aboutToExplode, Position(0, 0))
            arena.emplace(robotAttack, Position(x = 0, y = 1))

            val actions = listOf(
                Pair(aboutToExplode, Selfdestruction()),
                Pair(robotAttack, Attack(UP))
            ).shuffled()

            interpreter.execute(actions)

            robotAttack.health shouldBe 100 // suicide was killed so explosion is canceled
        }

        should("execute moves before attacks") {
            val arena = Arena(width = 12, height = 12)
            val interpreter = Interpreter(arena, presenter = null)

            val aboutToRunAway = Robot(owner = Player(), health = 100)
            val robotAttack = Robot(owner = Player(), health = 100)

            arena.emplace(aboutToRunAway, Position(0, 0))
            arena.emplace(robotAttack, Position(x = 0, y = 1))

            val actions = listOf(
                Pair(aboutToRunAway, Move(UP)),
                Pair(robotAttack, Attack(UP))
            ).shuffled()

            interpreter.execute(actions)

            aboutToRunAway.health shouldBe 100
        }
    }
})