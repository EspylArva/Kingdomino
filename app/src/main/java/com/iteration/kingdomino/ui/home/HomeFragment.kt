package com.iteration.kingdomino.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.components.RecyclerDotIndicator
import com.iteration.kingdomino.components.RecyclerViewMargin
import com.iteration.kingdomino.components.Utils
import com.iteration.kingdomino.csvreader.CSVReader
import com.iteration.kingdomino.game.Card
import com.iteration.kingdomino.game.Field
import com.iteration.kingdomino.game.Player
import timber.log.Timber
import java.util.*
import kotlin.Exception

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var deck : Stack<Card>
    private var choice = mutableListOf<Card>()
    private lateinit var players : List<Player>

    private lateinit var recyclerChoice : RecyclerView  // Available cards
    private lateinit var recyclerMaps : RecyclerView    // Players' field
    private lateinit var clHeader : ConstraintLayout    // Header with scores


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        players = listOf(Player("John Doe"), Player("Emily Lee"), Player("Joseph Staline"), Player("Chris Tabernacle"))
        deck = CSVReader().readCsv(requireContext())
        deck.shuffle()
        choice = drawCards()

        val root = initViews(inflater, container)
        setListeners()
        setObservers()

        debugWorld()

        return root
    }

    private fun setListeners() {
        clHeader.setOnClickListener {
            if(deck.size < 5)
            {
                Toast.makeText(requireContext(), "Empty deck! Shuffling...", Toast.LENGTH_SHORT).show()
                deck = CSVReader().readCsv(requireContext())
                deck.shuffle()
            }
            Timber.d("$choice")
            choice.clear()
            choice.addAll(drawCards())
            Timber.d("$choice")
            recyclerChoice.adapter!!.notifyDataSetChanged()
            Toast.makeText(requireContext(), "Clicked the information, refreshing choice deck", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setObservers() {
    }

    private fun initViews(inflater: LayoutInflater, container: ViewGroup?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerChoice = root.findViewById(R.id.recycler_card_choice)
        recyclerMaps = root.findViewById(R.id.recycler_player_field)
        clHeader = root.findViewById(R.id.cl_player_info)

        recyclerChoice.setHasFixedSize(true)
        recyclerChoice.adapter = CardChoiceAdapter(choice)
        val recyclerLayout = LinearLayoutManager(requireContext())
        recyclerLayout.orientation = LinearLayoutManager.HORIZONTAL
        recyclerChoice.layoutManager = recyclerLayout
        recyclerChoice.addItemDecoration(RecyclerViewMargin(Utils.pxToDp(2, requireContext()),4))

        recyclerMaps.setHasFixedSize(true)
        recyclerMaps.adapter = PlayerMapAdapter(players, this)
        val recyclerLayout2 = LinearLayoutManager(requireContext())
        recyclerLayout2.orientation = LinearLayoutManager.HORIZONTAL
        recyclerMaps.layoutManager = recyclerLayout2
        PagerSnapHelper().attachToRecyclerView(recyclerMaps)
        recyclerMaps.addItemDecoration(RecyclerDotIndicator(0xFFFFFFFFFF.toInt(), 0x66FFFFFF))

        return root
    }

    fun drawCards() : MutableList<Card> {
        if(deck.size < 4) throw DeckSizeException("Invalid deck size: current size is ${deck.size}, but it should be greater than 4 to draw cards.")
        val choice = mutableListOf<Card>()
        while(choice.size < 4)
        {
            choice.add(deck.pop())
        }
        return choice
    }

    fun playTile(playerPosition : Int, position : Pair<Int, Int>)
    {
        try {
            // Play tile
            players[playerPosition].map.addTile(choice[0].tile1, position)
            recyclerMaps.adapter!!.notifyItemChanged(playerPosition)

            // Refresh score
            // TODO
        } catch (e : Field.PlayerFieldException) { Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show() }
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