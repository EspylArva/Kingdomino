package com.iteration.kingdomino.game.model;

import com.google.gson.*
import timber.log.Timber
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.ZoneId
import java.time.ZonedDateTime
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
        lastUpdateDate
    }

    fun toJson() : String {
        Timber.d("Date: $creationDate ==> $lastUpdateDate")
        val json = GsonBuilder().registerTypeAdapter(LocalDateTime::class.java,
            JsonSerializer { date: LocalDateTime, _, _ -> return@JsonSerializer JsonPrimitive("${date}Z") })
            .create()
            .toJson(this)
        Timber.d("Date ==> $json")
        return json
    }

    override fun toString(): String {
        val summaryBuilder = StringBuilder()
        summaryBuilder.append("===== Game #$gameId (seed=$seed) =====\n")
            .append("    . modifiers: $modifiers\n")
            .append("    . players: ${players.map { it.name }}\n")
            .append("    . dates: $creationDate => $lastUpdateDate\n")
            .append("--- Game state ---\n") //TODO: Add game state
            .append("=============================================")


        return summaryBuilder.toString()
    }
}
