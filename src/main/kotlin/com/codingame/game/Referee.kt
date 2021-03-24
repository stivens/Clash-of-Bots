package com.codingame.game

import com.codingame.game.core.Arena
import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.MultiplayerGameManager
import com.google.inject.Inject

class Referee : AbstractReferee() {
    @Inject private lateinit var gameManager: MultiplayerGameManager<Player>
    private lateinit var presenter: Presenter
    private lateinit var arena: Arena

    override fun init() {
        arena = loadArena()
        presenter = Presenter(arena)
        TODO("Not yet implemented")
    }

    override fun gameTurn(turn: Int) {
        TODO("Not yet implemented")
    }

    private fun loadArena(): Arena = TODO("Not yet implemented")
}