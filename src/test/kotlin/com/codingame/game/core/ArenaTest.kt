package com.codingame.game.core

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ArenaTest : FeatureSpec({
    feature("Arena.of (a.k.a parser)") {
        scenario("should properly parse the map") {
            val raw =
                """
                |   ########
                |   #.....E#
                |   ########
                """.trimMargin()

            val arena = Arena.of(raw)

            arena.width shouldBe 11
            arena.height shouldBe 3

            arena.board shouldBe arrayOf(
                VOID, VOID, VOID, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL,
                VOID, VOID, VOID, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ENTRY, WALL,
                VOID, VOID, VOID, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL,
            )
        }
    }
})