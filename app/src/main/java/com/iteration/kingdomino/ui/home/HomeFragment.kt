package com.iteration.kingdomino.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.iteration.kingdomino.R
import com.iteration.kingdomino.csvreader.CSVReader
import com.iteration.kingdomino.game.Card
import com.iteration.kingdomino.game.Field
import com.iteration.kingdomino.game.Player
import com.iteration.kingdomino.game.Tile
import timber.log.Timber
import java.util.*
import kotlin.Exception

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var deck : Stack<Card>
    private lateinit var choice : List<Card>
    private lateinit var players : List<Player>

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        players = listOf(Player("John Doe"), Player("Emily Lee"), Player("Joseph Staline"), Player("Chris Tabernacle"))

        deck = CSVReader().readCsv(requireContext())
        deck.shuffle()

        // Template data
//        deck.add(Card(1, Tile(Tile.Terrain.FIELD, 2), Tile(Tile.Terrain.FOREST, 0)))
//        deck.add(Card(2, Tile(Tile.Terrain.FIELD, 1), Tile(Tile.Terrain.FOREST, 1)))
//        deck.add(Card(3, Tile(Tile.Terrain.FIELD, 1), Tile(Tile.Terrain.PLAIN, 2)))
//        deck.add(Card(4, Tile(Tile.Terrain.SEA, 2), Tile(Tile.Terrain.PLAIN, 0)))
//        deck.add(Card(5, Tile(Tile.Terrain.SEA, 1), Tile(Tile.Terrain.FOREST, 0)))
//        deck.add(Card(6, Tile(Tile.Terrain.SEA, 0), Tile(Tile.Terrain.FIELD, 1)))
//        deck.add(Card(7, Tile(Tile.Terrain.MINE, 2), Tile(Tile.Terrain.FIELD, 1)))
//        deck.add(Card(8, Tile(Tile.Terrain.MINE, 3), Tile(Tile.Terrain.FOREST, 0)))

        choice = drawCards()

        debugWorld()

        players[0].map.addTile(choice[0].tile1, Pair(3,4))
        players[0].debugPlayer()



//         3 0
//         0 0

//        try{
//            players[0].map.addTile(choice[0].tile1, Pair(5,2)) // PlayerFieldException: Impossible to add this tile to the player field: given x index was 5; should be between 0 and 2
//        }
//        catch(e : Field.PlayerFieldException) {Timber.e(e)}
//        try {
//            players[0].map.addTile(choice[0].tile2, Pair(0,3)) // PlayerFieldException: Impossible to add this tile to the player field: given y index was 3; should be between 0 and 2
//        }
//        catch(e : Field.PlayerFieldException) {Timber.e(e)}

//        players[0].map.addTile(choice[0].tile2, Pair(2,2))
//        players[0].debugPlayer()
//
//        val test = mutableListOf(0, 1)
//        test.add(2, 56)
//        Timber.d(test.toString())
//
//
//        players[0].map.addTile(choice[1].tile1, Pair(3,2))
//        players[0].debugPlayer()





        return root
    }

    fun drawCards() : List<Card> {
        if(deck.size < 4) throw DeckSizeException("Invalid deck size: current size is ${deck.size}, but it should be greater than 4 to draw cards.")
        val choice = mutableListOf<Card>()
        while(choice.size < 4)
        {
            choice.add(deck.pop())
        }
        return choice
    }

    fun debugWorld()
    {
        Timber.e("======== Common =========")
        Timber.d("Current deck: $deck")
        Timber.d("Current choice: $choice")
        Timber.e("=========================")
        Timber.e("======== Players ========")
        for(p in players)
        {
            p.debugPlayer()
        }
        Timber.e("=========================")
    }


    class DeckSizeException(message : String) : Exception(message)
}