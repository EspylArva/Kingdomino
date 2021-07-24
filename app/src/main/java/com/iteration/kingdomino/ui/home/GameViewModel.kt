package com.iteration.kingdomino.ui.home

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iteration.kingdomino.csvreader.CSVReader
import com.iteration.kingdomino.game.Card
import com.iteration.kingdomino.game.Field
import com.iteration.kingdomino.game.Player
import timber.log.Timber
import java.util.*

class GameViewModel(val app : Application) : AndroidViewModel(app) {

    lateinit var deck : Stack<Card>
    private var _choice = MutableLiveData<MutableList<Card>>().apply { value = mutableListOf() }
    private var _players = MutableLiveData<List<Player>>().apply {
        value = listOf(Player("John Doe"), Player("Emily Lee"), Player("Joseph Staline"), Player("Chris Tabernacle"))
    }
    var updatePlayer = MutableLiveData<Int>()

    val choice : LiveData<MutableList<Card>> = _choice
    val players : LiveData<List<Player>> = _players

    init {
        setDeck()
        drawCards()
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
        try {
            // Play tile
            _players.value!![playerPosition].map.addTile(choice.value!![0].tile1, position)
            updatePlayer.value = playerPosition

            // Refresh score
            // TODO
        } catch (e : Field.PlayerFieldException) { Toast.makeText(app.applicationContext, e.message, Toast.LENGTH_SHORT).show() }
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