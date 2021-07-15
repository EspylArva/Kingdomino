package com.iteration.kingdomino.game

import timber.log.Timber

class Player(val name : String) {
    val map = Field()


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

}