package com.codingame.game.core

import com.codingame.game.Player
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ArenaTest : FeatureSpec({
    feature("Arena.putNewRobot") {
        val arena = Arena(width = 12, height = 12)

        scenario("robot should be present on the board after method invocation") {
            val pos = Position(5, 5)
            val robot = Robot(owner = Player(), health = 100)

            arena.get(pos) shouldBe null

            arena.putNewRobot(robot, pos)

            arena.get(pos) shouldBe robot
        }
    }
})