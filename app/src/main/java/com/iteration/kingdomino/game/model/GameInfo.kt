package com.iteration.kingdomino.game.model;
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import kotlin.random.Random

data class GameInfo(
    val gameId: Int,
    val players: Collection<Player>,
    val modifiers: Set<String>) {

    val deck: Set<Card> = HashSet()
    var choice: MutableList<Card> = mutableListOf()

    val seed: Int = generateNewSeed()
    val creationDate: LocalDateTime = now()
    var lastUpdateDate: LocalDateTime = now()
    var gameEnded: Boolean = false

    private fun generateNewSeed(): Int {
        // FIXME: get the list of seeds from DAO
        val seeds = HashSet<Int>()
        return sequence<Int> { (1.. 65536).random() }.first { !seeds.contains(it) }
    }

    fun update() {
        // FIXME
        lastUpdateDate = now()
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }
}
