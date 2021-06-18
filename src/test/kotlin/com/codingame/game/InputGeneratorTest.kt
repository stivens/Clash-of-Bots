package com.codingame.game

import com.codingame.game.core.Arena
import com.codingame.game.core.Position
import com.codingame.game.core.Robot
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class InputGeneratorTest : ShouldSpec({
    context("::minimapFor") {
        val arena = Arena(width = 12, height = 12)

        val player1 = Player()
        val player2 = Player()

        val robot1 = Robot(owner = player1, health = 100)
        val robot2 = Robot(owner = player1, health = 100)
        val robot3 = Robot(owner = player1, health =  50)

        val robot4 = Robot(owner = player2, health = 100)
        val robot5 = Robot(owner = player2, health =  42)

        listOf(
            Pair(robot1, Position(3, 3)),
            Pair(robot2, Position(5, 8)),
            Pair(robot3, Position(0, 0)),
            Pair(robot4, Position(3, 4)),
            Pair(robot5, Position(9, 9)),
        ).forEach { (robot, position) -> arena.emplace(robot, position) }

        should("return proper minimap for given robot") {
            InputGenerator.minimapFor(robot1, arena, visionRange = 1) shouldBe """
                0 0 0
                0 100 0
                0 -100 0
            """.trimIndent()

            InputGenerator.minimapFor(robot2, arena, visionRange = 1) shouldBe """
                0 0 0
                0 100 0
                0 0 0
            """.trimIndent()

            InputGenerator.minimapFor(robot3, arena, visionRange = 1) shouldBe """
                0 0 0
                0 50 0
                0 0 0
            """.trimIndent()

            InputGenerator.minimapFor(robot4, arena, visionRange = 3) shouldBe """
                0 0 0 0 0 0 0
                0 0 0 0 0 0 0
                0 0 0 -100 0 0 0
                0 0 0 100 0 0 0
                0 0 0 0 0 0 0
                0 0 0 0 0 0 0
                0 0 0 0 0 0 0
            """.trimIndent()

            InputGenerator.minimapFor(robot5, arena, visionRange = 3) shouldBe """
                0 0 0 0 0 0 0
                0 0 0 0 0 0 0
                0 0 0 0 0 0 0
                0 0 0 42 0 0 0
                0 0 0 0 0 0 0
                0 0 0 0 0 0 0
                0 0 0 0 0 0 -50
            """.trimIndent()
        }
    }
})