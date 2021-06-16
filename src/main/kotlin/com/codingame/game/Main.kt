package com.codingame.game

import com.codingame.game.core.Position
import com.codingame.game.core.Robot
import com.codingame.gameengine.runner.MultiplayerGameRunner

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val gameRunner = MultiplayerGameRunner()
        gameRunner.addAgent(TestAgent::class.java)
        //gameRunner.addAgent(Player2::class.java)

        // gameRunner.addAgent("python3 /home/user/player.py");

        // The first league is classic tic-tac-toe
        //gameRunner.setLeagueLevel(1)
        // The second league is ultimate tic-tac-toe
        // gameRunner.setLeagueLevel(2);

        gameRunner.start()
    }
}