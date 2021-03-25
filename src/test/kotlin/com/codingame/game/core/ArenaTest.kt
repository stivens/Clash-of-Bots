package com.codingame.game.core

import com.codingame.game.Player
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ArenaTest : FeatureSpec({
    feature("Arena.of (a.k.a parser)") {
        scenario("should properly parse the map") {
            val raw =
                """
                |--------------
                |---########---
                |---#.....E#---
                |---########---
                |--------------
                """.trimMargin()

            val arena = Arena.of(raw)

            arena.width shouldBe 14
            arena.height shouldBe 5

            arena.board shouldBe arrayOf(
                VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID,
                VOID, VOID, VOID, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, VOID, VOID, VOID,
                VOID, VOID, VOID, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ENTRY, WALL, VOID, VOID, VOID,
                VOID, VOID, VOID, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, VOID, VOID, VOID,
                VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID, VOID,
            )
        }
    }

    feature("Arena.putNewRobot") {
        val arena = this.javaClass.getResource("/arenas/testarena.txt").readText().let { Arena.of(it) }


        scenario("robot should be present on the board after method invocation") {
            val pos = Position(5, 5)
            val robot = Robot(owner = Player(), id = 42, position = pos, health = 100)

            arena.get(pos) shouldBe EMPTY

            arena.putNewRobot(robot, pos)

            arena.get(pos) shouldBe robot
        }
    }
})