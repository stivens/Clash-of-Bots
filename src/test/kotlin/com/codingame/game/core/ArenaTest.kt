package com.codingame.game.core

import com.codingame.game.Player
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ArenaTest : FeatureSpec({
    val arena = Arena(width = 12, height = 12)

    feature("Arena.emplace") {
        scenario("robot should be present on the board after method invocation") {
            val pos = Position(5, 5)
            val robot = Robot(owner = Player())

            arena.emplace(robot, pos)

            arena.get(pos) shouldBe robot
            arena.getPositionOf(robot) shouldBe pos
        }

        scenario("robot position should be updated if it was already on the board") {
            val pos1 = Position(5, 5)
            val pos2 = Position(9, 9)
            val robot = Robot(owner = Player())

            arena.emplace(robot, pos1)
            arena.emplace(robot, pos2)

            arena.getPositionOf(robot) shouldBe pos2
            arena.get(pos1) shouldBe null
            arena.get(pos2) shouldBe robot
        }

        scenario("robot should be spawn-killed if it occupies target position") {
            val pos = Position(0, 0)
            val robot1 = Robot(owner = Player())
            val robot2 = Robot(owner = Player())

            arena.emplace(robot1, pos)
            arena.emplace(robot2, pos)

            arena.get(pos) shouldBe robot2
            arena.getPositionOf(robot1) shouldBe null
        }
    }

    feature("Arena.remove") {
        scenario("robot should not be present on the board after method invocation") {
            val pos = Position(2, 2)
            val robot = Robot(owner = Player())

            arena.emplace(robot, pos)
            arena.remove(robot)

            arena.get(pos) shouldBe null
            arena.getPositionOf(robot) shouldBe null
        }
    }
})