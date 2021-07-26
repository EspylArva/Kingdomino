package com.iteration.kingdomino.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.components.GameHeader
import com.iteration.kingdomino.components.RecyclerDotIndicator
import com.iteration.kingdomino.components.RecyclerViewMargin
import com.iteration.kingdomino.components.Utils
import timber.log.Timber

class GameFragment : Fragment() {

    private lateinit var vm: GameViewModel



    private lateinit var recyclerChoice : RecyclerView  // Available cards
    private lateinit var recyclerMaps : RecyclerView    // Players' field
    private lateinit var clHeader : GameHeader          // Header with scores


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        vm = ViewModelProvider(this).get(GameViewModel::class.java)

        val root = initViews(inflater, container)
        setListeners()
        setObservers()

//        debugWorld()

        return root
    }

    private fun setListeners() {
        clHeader.setOnClickListener {
            if(vm.deck.size < 5)
            {
                Toast.makeText(requireContext(), "Empty deck! Shuffling...", Toast.LENGTH_SHORT).show()
                vm.setDeck()
            }
            vm.drawCards()
            Toast.makeText(requireContext(), "Drawing 4 new cards...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setObservers() {
        vm.choice.observe(viewLifecycleOwner, Observer {
            Timber.d("Observed that choice set changed")
            recyclerChoice.adapter!!.notifyDataSetChanged()
        })

        vm.players.observe(viewLifecycleOwner, Observer {
            Timber.d("Observed that players changed")
            recyclerMaps.adapter!!.notifyDataSetChanged()//notifyItemChanged(playerPosition)
        })

        vm.updatePlayer.observe(viewLifecycleOwner, Observer {
            recyclerMaps.adapter!!.notifyItemChanged(it)
            clHeader.listPlayerInfo[it].second.text = resources.getString(R.string.player_score, vm.players.value!![it].getScore())

        })
    }

    private fun initViews(inflater: LayoutInflater, container: ViewGroup?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerChoice = root.findViewById(R.id.recycler_card_choice)
        recyclerMaps = root.findViewById(R.id.recycler_player_field)
        clHeader = root.findViewById(R.id.cl_player_info)

        recyclerChoice.setHasFixedSize(true)
        recyclerChoice.adapter = CardChoiceAdapter(vm.choice)
        val recyclerLayout = LinearLayoutManager(requireContext())
        recyclerLayout.orientation = LinearLayoutManager.HORIZONTAL
        recyclerChoice.layoutManager = recyclerLayout
        recyclerChoice.addItemDecoration(RecyclerViewMargin(Utils.pxToDp(2, requireContext()),4))

        recyclerMaps.setHasFixedSize(true)
        recyclerMaps.adapter = PlayerMapAdapter(vm.players, vm)
        val recyclerLayout2 = LinearLayoutManager(requireContext())
        recyclerLayout2.orientation = LinearLayoutManager.HORIZONTAL
        recyclerMaps.layoutManager = recyclerLayout2
        PagerSnapHelper().attachToRecyclerView(recyclerMaps)
        recyclerMaps.addItemDecoration(RecyclerDotIndicator(0xFFFFFFFFFF.toInt(), 0x66FFFFFF))

        for(i in 0..3) {
            clHeader.listPlayerInfo[i].first.text = vm.players.value!![i].name
        }

        return root
    }
}