package com.iteration.kingdomino.ui.home

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iteration.kingdomino.R
import com.iteration.kingdomino.components.LoopingList
import com.iteration.kingdomino.components.loopingListOf
import com.iteration.kingdomino.csvreader.CSVReader
import com.iteration.kingdomino.game.Card
import com.iteration.kingdomino.game.Field
import com.iteration.kingdomino.game.Player
import com.iteration.kingdomino.game.Tile
import timber.log.Timber
import java.util.*
import java.util.stream.Collectors.toList
import java.util.stream.Collectors.toMap
import kotlin.collections.LinkedHashMap

class GameViewModel(val app : Application) : AndroidViewModel(app) {

    /**
     * Deck containing all the cards; cards are drawn from the deck to form the choice deck
     */
    lateinit var deck : Stack<Card>

    /**
     * List of cards to play which the players choose from
     */
    var choice = MutableLiveData<LinkedHashMap<Card, Boolean>>().apply { value = LinkedHashMap() }

    /**
     * Ordered list of players
     */
    var players : LoopingList<Player> = loopingListOf(Player("John Doe"), Player("Emily Lee"), Player("Joseph Staline"), Player("Chris Tabernacle"))
    var immutablePlayers : List<Player>

    /**
     * Which card has been picked by the player
     */
    var playerCardSelection = MutableLiveData<Card>().apply { value = null }

    /**
     * Picked positions. Should never have more than two positions
     */
    val playerPickedPositions = MutableLiveData<MutableList<Pair<Int, Int>>>().apply { value = mutableListOf() }

    init {
        Timber.d("Initializing Game...")

        Timber.d("Players=${players}")
        setDeck()       // Draw and shuffle the 48 cards deck
        Timber.d("Deck=$deck")
        drawCards()     // Draw 4 cards from the deck to form the choice deck
        Timber.d("Choice=$choice")

        immutablePlayers = players.value!!.toList()

                        // TODO Shuffle the players
    }

    fun drawCards() {
        if(deck.size == 0) {

        }
        if(deck.size < 4) {
            throw DeckSizeException("Invalid deck size: current size is ${deck.size}, but it should be greater than 4 to draw cards.")
        }

        Timber.v("Before: deck size: ${deck.size}\nchoice = ${choice.value}")
        choice.value!!.clear()
        val newDraw = mutableListOf<Card>()
        while(newDraw.size < 4)
        {
            newDraw.add(deck.pop())
        }
        newDraw.sorted().forEach {
            choice.value!![it] = true
        }
        Timber.v("After : deck size: ${deck.size}\nchoice = ${choice.value}")
        choice.postValue(choice.value)
    }

    fun setDeck()
    {
        deck = CSVReader().readCsv(app.applicationContext)
        deck.shuffle()
    }

    /**
     * After the tile has been placed using the button Confirm, the current player's turn ends.
     * We evaluate the game state to loop to the next player
     */
    fun endPlayerTurn() {
        // Play
        try {
            Timber.d("Playing a card: card=${playerCardSelection.value} at positions=${playerPickedPositions.value}")
            players.value!![0].playCard(playerCardSelection.value!!, playerPickedPositions.value!![0], playerPickedPositions.value!![1])
        } catch (e: Exception) {
            Timber.e("Error: $e")
        }
        choice.value!![playerCardSelection.value!!] = false
        choice.postValue(choice.value!!)

        if(choice.value!!.all { !it.value }) { // All cards have been played
            drawCards()
        }
        // Reset picked
        playerCardSelection.value = null

        // Check if re-drawing is needed
        // Change player
        players.cycle()
    }

    fun debugWorld()
    {
        Timber.e("======== Common =========")
        Timber.d("Current deck: $deck")
        Timber.d("Current choice: $choice")
        Timber.e("=========================")
        Timber.e("======== Players ========")
        for(p in players.value!!)
        {
            p.debugPlayer()
        }
        Timber.e("=========================")
    }

    fun addPosition(row: Int, col: Int) {
        when(playerPickedPositions.value!!.size) {
            0, 1 -> {
                playerPickedPositions.value!!.add(Pair(row, col))
                playerPickedPositions.postValue(playerPickedPositions.value)
            }
            2 -> {
                playerPickedPositions.value!!.clear()
                playerPickedPositions.value!!.add(Pair(row, col))
                playerPickedPositions.postValue(playerPickedPositions.value)
            }
            else -> throw GameBusinessLogicException("Picked positions should never exceed size 2: currently ${playerPickedPositions.value!!.size} (${playerPickedPositions.value!!})")
        }
    }

    class DeckSizeException(message: String) : Exception(message)
    class GameBusinessLogicException(message: String) : Exception(message)
}