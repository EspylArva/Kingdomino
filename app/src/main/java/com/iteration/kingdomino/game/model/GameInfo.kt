package com.iteration.kingdomino.game.model;
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.*

data class GameInfo(
    val gameId: UUID,
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

    fun toJson() : String = Gson().toJson(this)

    override fun toString(): String {
        val summaryBuilder = StringBuilder()
        summaryBuilder.append("===== Game #$gameId (seed=$seed) =====\n")
            .append("    . modifiers: $modifiers\n")
            .append("    . players: ${players.map { it.name }}\n")
            .append("--- Game state ---\n") //TODO: Add game state
            .append("=============================================")


        return summaryBuilder.toString()
    }
}
