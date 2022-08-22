package com.iteration.kingdomino.game

import androidx.lifecycle.MutableLiveData
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap

class Player(val name : String) : MutableLiveData<Player>() {
    val map = Field()

    fun playCard(card: Card, tile1Position: Pair<Int, Int>, tile2Position: Pair<Int, Int>) {
        postValue(this)
        map.addTile(card.tile1, tile1Position)
        map.addTile(card.tile2, tile2Position)
    }

    fun getScore() : Int
    {
        return map.calculateScore()
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
        Timber.d("Player $name currently has a score of ${getScore()}.")
        Timber.d("Representation of their board:\n$map")
        // TODO
    }

    override fun toString(): String {
        return "[Player: name=$name currentScore=${getScore()}]"
    }

}