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
import com.iteration.kingdomino.game.*
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
    var players = MutableLiveData<MutableList<Player>>().apply { value = mutableListOf() }
    var playerOrder : MutableMap<Player, Int>

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

        playerOrder = mutableMapOf(Pair(Player("John Doe"), 1), Pair(Player("Emily Lee"), 2), Pair(Player("Joseph Staline"), 3), Pair(Player("Chris Tabernacle"), 4))
        Timber.d("Players=${playerOrder}")

        setDeck()       // Draw and shuffle the 48 cards deck
        Timber.d("Deck=$deck")

        drawCards()     // Draw 4 cards from the deck to form the choice deck
        Timber.d("Choice=$choice")

        regeneratePlayerList()
//        players.value = playerOrder.keys.toMutableList()
//        playerOrder = players.value!!.toMutableList()
//                         TODO Shuffle the players
    }

    fun drawCards() {
        if(deck.size < 4) {
            // FIXME: show game ending results
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
        val player = players.value!![0]
        val card = playerCardSelection.value
        val positions = playerPickedPositions.value!!
        Timber.d("Ending turn of $player. Playing $card at $positions")
        play(player, card, positions)

        // Check if re-drawing is needed
        if(players.value!!.size == 0) {
            drawCards()
            regeneratePlayerList()
            Timber.d("Regenerating player list.\nplayerList=${players.value}\nplayerOrder=$playerOrder")
        }

        players.postValue(players.value)
        Timber.i("End of $player's turn. Switching to ${players.value!![0]}'s turn.")
    }

    fun regeneratePlayerList() {
        players.value = playerOrder.entries.stream()
                .sorted { entry1, entry2 -> entry1.value - entry2.value}
                .map { entry -> entry.key }
                .collect(toList())
    }

    private fun play(player: Player, card: Card?, positions: MutableList<Pair<Int, Int>>) {
        if(card != null) {
            try {
                Timber.d("Playing a card: card=$card at positions=$positions")
                players.value!![0].playCard(card, positions[0], positions[1])
            } catch (e: Exception) {
                Timber.e("Error: $e")
            }
            playerOrder[player] = card.id
            Timber.d("Player $player found=${playerOrder.containsKey(player)}, index=${playerOrder[player]}")
            choice.value!![card] = false
            choice.postValue(choice.value!!)
        } else {
            playerOrder[player] = (Math.random()*100+50).toInt()
        }
        playerCardSelection.value = null
        players.value!!.removeAt(0)
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
        if(playerPickedPositions.value == null) return
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