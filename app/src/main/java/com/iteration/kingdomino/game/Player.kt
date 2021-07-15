package com.iteration.kingdomino.game

import timber.log.Timber

public class Player(val name : String) {
    var score : Int = 0
    val map = Field()


    fun computeScore() : Int
    {
        // TODO
        var score = 0



        return 0
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
        Timber.d("Player $name currently has a score of $score.")
        Timber.d("Representation of their board:\n$map")
        // TODO
    }

}