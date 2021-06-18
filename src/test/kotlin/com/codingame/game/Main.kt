package com.codingame.game

import com.codingame.gameengine.runner.MultiplayerGameRunner

private val DEFAULT_AI = arrayOf("python3", "config/Boss.py")

fun main() {
    val gameRunner = MultiplayerGameRunner()
    gameRunner.addAgent(DEFAULT_AI, "Bob")
    gameRunner.addAgent(DEFAULT_AI, "Alice")
    gameRunner.start()
}
