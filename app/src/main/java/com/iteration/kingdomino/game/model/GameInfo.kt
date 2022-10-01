package com.iteration.kingdomino.game.model;
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.LocalDateTime.now

data class GameInfo(
    val gameId: Int,
    val players: Collection<Player>,
    val modifiers: Set<String>,
    var seed: Int ) {

    val deck: Set<Card> = HashSet()
    var choice: MutableList<Card> = mutableListOf()

    val creationDate: LocalDateTime = now()
    var lastUpdateDate: LocalDateTime = now()



    fun update() {
        // FIXME
        lastUpdateDate = now()
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }
}
