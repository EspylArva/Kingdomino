package com.iteration.kingdomino.ui.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.iteration.kingdomino.csvreader.CSVReader
import com.iteration.kingdomino.game.*
import timber.log.Timber
import java.util.*
import java.util.stream.Collectors.toList
import kotlin.collections.LinkedHashMap

class GameViewModel(val app : Application) : AndroidViewModel(app) {

    /**
     * Deck containing all the cards; cards are drawn from the deck to form the choice deck
     */
    lateinit var deck : Stack<Card>

    /**
     * Map of drawn cards to play, which the players choose cards from.
     *
     * Key: Card.       The card.
     * Value: Boolean.  true: card is available for play. false: card has already been played.
     */
    var choice = MutableLiveData<LinkedHashMap<Card, Boolean>>().apply { value = LinkedHashMap() }

    /**
     * Ordered list of remaining players who has not played yet this turn.
     */
    var players = MutableLiveData<MutableList<Player>>().apply { value = mutableListOf() }

    /**
     * Map of players with an index to determine player order.
     * On the first turn, a random index is generated to randomise player order.
     * On following turns, player index is based on last turn's selected card id.
     *
     * Key: Player.     The player.
     * Value: Int.      The index. Lower index means the player play first.
     */
    var playerOrder : MutableMap<Player, Int>

    /**
     * Gets the current player. // FIXME: replace usages of players.value!![0] by currentPlayer
     */
    val currentPlayer : Player?
        get() = players.value!!.first()

    /**
     * FIXME: see if this works properly? Is index modified when a card is played?
     */
    val currentPlayerIndex : Int
        get() {
            return playerOrder.entries.stream()
                    .map { entry -> entry.key }
                    .collect(toList())
                    .indexOf(currentPlayer)
        }

    /**
     * Which card has been picked by the player
     */
    var playerCardSelection = MutableLiveData<Card>().apply { value = null }

    /**
     * Picked positions. Should never have more than two positions
     */
    val playerPickedPositions = MutableLiveData<MutableList<Pair<Int, Int>>>().apply { value = mutableListOf() }

    init {
        Timber.i("Initializing Game... (Start)")

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
        Timber.i("Initializing Game... (End)")
    }

    /**
     * Draws a new card selection.
     * Should be used whenever all players ended their turn.
     * Cards are drawn from [deck], and are added to [choice].
     */
    fun drawCards() {
        if(deck.size < 4) {
            // FIXME: show game ending results
            Timber.e("End results: ${playerOrder.keys}")
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

    /**
     * Builds the [deck].
     * Should only be used when initialising the game.
     */
    private fun setDeck()
    {
        deck = CSVReader().readCsv(app.applicationContext)
        deck.shuffle()
    }

    /**
     * Ends the current player turn.
     * After the tile has been placed using the button Confirm, the current player's turn ends.
     * We evaluate the game state to loop to the next player.
     */
    fun endPlayerTurn() {
        val player = players.value!![0]
        val card = playerCardSelection.value
        val positions = playerPickedPositions.value!!

        Timber.i("Ending turn of $player (Start).")
        if(card != null && positions.size == 2){
            Timber.d("Playing $card at $positions")
            play(player, card, positions)
        }

        // Check if re-drawing is needed
        if(players.value!!.size == 0) {
            drawCards()
            regeneratePlayerList()
            Timber.d("""
                |All players played once. Regenerating player list:
                |    . new playerList=${players.value}
                |    . new playerOrder=$playerOrder
                |    . new choice=${choice.value}
            """.trimIndent())
        }
        players.postValue(players.value)

        Timber.d("Resetting game state (playerCardSelection.")
        playerCardSelection.value = null
        playerPickedPositions.value = mutableListOf()

        Timber.i("Ending turn of $player (End). Switching to ${players.value!![0]}'s turn.")
    }

    /**
     * Sets [players] value to the ordered list extracted from [playerOrder], based on the previously selected cards
     */
    private fun regeneratePlayerList() {
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

    @Throws(GameBusinessLogicException::class)
    fun addPosition(row: Int, col: Int) {
        if(playerPickedPositions.value == null) return
        when (playerPickedPositions.value!!.size) {
            0 -> playerPickedPositions.value!!.add(Pair(row, col))
            1 -> {
                val firstPickedPos = Pair(playerPickedPositions.value!![0].first, playerPickedPositions.value!![0].second)
                val secondPickedPos = Pair(row, col)
                if(!players.value!![0].map.isCardTilesAdjacent(firstPickedPos, secondPickedPos)) {
                    playerPickedPositions.value!!.clear()
                }
                playerPickedPositions.value!!.add(Pair(row, col))
            }
            2 -> {
                playerPickedPositions.value!!.clear()
                playerPickedPositions.value!!.add(Pair(row, col))
            }
            else -> throw GameBusinessLogicException("Picked positions should never exceed size 2: currently ${playerPickedPositions.value!!.size} (${playerPickedPositions.value!!})")
        }
        playerPickedPositions.postValue(playerPickedPositions.value)
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

    class DeckSizeException(message: String) : Exception(message)
    class GameBusinessLogicException(message: String) : Exception(message)
}