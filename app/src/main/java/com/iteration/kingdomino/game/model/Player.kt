package com.iteration.kingdomino.game.model

import androidx.lifecycle.MutableLiveData
import timber.log.Timber

//@Entity
data class Player(val name : String) : MutableLiveData<Player>() {
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
        // TODO
    }

    override fun toString(): String {
        return "[Player: name=$name currentScore=${score}]"
    }
}