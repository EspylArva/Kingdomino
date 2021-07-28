package com.iteration.kingdomino.ui.home

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iteration.kingdomino.R
import com.iteration.kingdomino.csvreader.CSVReader
import com.iteration.kingdomino.game.Card
import com.iteration.kingdomino.game.Field
import com.iteration.kingdomino.game.Player
import timber.log.Timber
import java.util.*

class GameViewModel(val app : Application) : AndroidViewModel(app) {

    private var _choice = MutableLiveData<MutableList<Card>>().apply { value = mutableListOf() }
    private var _players = MutableLiveData<List<Player>>().apply {
        value = listOf(Player("John Doe"), Player("Emily Lee"), Player("Joseph Staline"), Player("Chris Tabernacle"))
    }

    lateinit var deck : Stack<Card>                                             // Deck containing all the cards; cards are drawn from the deck to form the choice deck
    var updatePlayer = MutableLiveData<Int>()                                   // Which player just played; should be a value between 0 and players.size
    var cardSelectionPosition = MutableLiveData<Int>().apply { value = -1 }     // Which card has been picked by the player
    var availableCardsInChoice = MutableLiveData<MutableList<Int>>()            // List of cards in the choice list which have not been played yet
    val choice : LiveData<MutableList<Card>> = _choice                          // List of cards to play which the players choose from
    val players : LiveData<List<Player>> = _players                             // Ordered list of players

    init {
        setDeck()       // Draw and shuffle the 48 cards deck
        drawCards()     // Draw 4 cards from the deck to form the choice deck

                        // TODO Shuffle the players
    }

    fun drawCards() {
        if(deck.size < 4) {
            throw DeckSizeException("Invalid deck size: current size is ${deck.size}, but it should be greater than 4 to draw cards.")
        }
        val newDraw = mutableListOf<Card>()
        while(newDraw.size < 4)
        {
            newDraw.add(deck.pop())
        }
        newDraw.sort()
        availableCardsInChoice.value = mutableListOf(0, 1, 2, 3)
        cardSelectionPosition.value = -1
//        _choice.value!!.clear()
        _choice.value = newDraw
    }

    fun setDeck()
    {
        deck = CSVReader().readCsv(app.applicationContext)
        deck.shuffle()
    }

    fun playTile(playerPosition : Int, position : Pair<Int, Int>)
    {
        if(cardSelectionPosition.value != -1)
        {
            try {
                _players.value!![playerPosition].map.addTile(choice.value!![cardSelectionPosition.value!!].tile1, position)
                availableCardsInChoice.value!!.remove(cardSelectionPosition.value!!)
                availableCardsInChoice.value = availableCardsInChoice.value
                cardSelectionPosition.value = -1
                updatePlayer.value = playerPosition
            } catch (e : Field.PlayerFieldException) { Toast.makeText(app.applicationContext, app.applicationContext.resources.getString(R.string.error_play_tile), Toast.LENGTH_SHORT).show() }
        }
    }

    fun debugWorld()
    {
        Timber.e("======== Common =========")
        Timber.d("Current deck: $deck")
        Timber.d("Current choice: $choice")
        Timber.e("=========================")
        Timber.e("======== Players ========")
        for(p in _players.value!!)
        {
            p.debugPlayer()
        }
        Timber.e("=========================")
    }

    class DeckSizeException(message : String) : Exception(message)
}