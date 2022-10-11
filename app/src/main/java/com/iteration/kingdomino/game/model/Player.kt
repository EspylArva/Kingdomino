package com.iteration.kingdomino.game.model

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import timber.log.Timber

data class Player(val name : String) : MutableLiveData<Player>() {

    constructor(playerData: Player.Data) : this(playerData.name) {
        // FIXME: this should be the primary constructor
    }

    val map = Field(fieldSize=5)
    val score: Int
        get() = map.calculateScore()

    fun playCard(card: Card, tile1Position: Pair<Int, Int>, tile2Position: Pair<Int, Int>) {
        map.addCard(card, tile1Position, tile2Position)
        postValue(this)
    }

    fun computeChoices(card : Card) : List<Pair<Int, Int>>
    {
        // TODO
        return listOf()
    }

    fun listAllChoices(cards : List<Card>) : HashMap<Card, List<Pair<Int, Int>>>
    {
        val choices = HashMap<Card, List<Pair<Int, Int>>>()
        for(card in cards) {
            choices[card] = computeChoices(card)
        }
        return choices
    }

    fun debugPlayer() {
        Timber.d("Player $name currently has a score of ${score}.")
        Timber.d("Representation of their board:\n$map")
    }

    override fun toString(): String {
        return "[Player: name=$name currentScore=${score}]"
    }

    fun getPointDiffFor(card: Card, positions: MutableList<Pair<Int, Int>>): Int {
        Timber.d("Point diff: card=$card positions=$positions")
        val possibleMap = map.clone()
        if (positions.isNotEmpty()) {
            possibleMap.addTile(card.tile1, positions[0])
        }
        if (positions.size > 1) {
            possibleMap.addTile(card.tile2, positions[1])
        }

        val size = possibleMap.field.size
        val maps = (0 until size).joinToString("\n") {
            "${map.field.mapAsString().split("\n")[it]}   ==>   ${possibleMap.field.mapAsString().split("\n")[it]}"
        }

        Timber.d("Point diff for $name: current=${map.calculateScore()} ==> possible=${possibleMap.calculateScore()})\n$maps")

        return possibleMap.calculateScore() - map.calculateScore()
    }

    data class Data(var name: String) {
        var type: Type = Type.Human
    }

    enum class Type {
        Human("Human"),
        AI_easy("AI (easy)"),
        AI_hard("AI (hard)"),
        AI_brutal("AI (brutal)");

        val label: String

        constructor(l: String) {
            label = l
        }

        companion object {
            fun parse(str: String) : Type {
                return values().find { it.label == str } ?: Human
            }
        }
    }
}