package com.iteration.kingdomino.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.components.*
import timber.log.Timber

class GameFragment : Fragment() {

    private lateinit var vm: GameViewModel



    private lateinit var recyclerChoice : RecyclerView  // Available cards
    private lateinit var recyclerMaps : RecyclerView    // Players' field
    private lateinit var clHeader : GameHeader          // Header with scores
    private lateinit var buttonCancel : Button
    private lateinit var buttonConfirm : Button

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
            vm.drawCards()
            Toast.makeText(requireContext(), resources.getString(R.string.drawing_cards), Toast.LENGTH_SHORT).show()
        }

        buttonCancel.setOnClickListener {
            vm.playerCardSelection.value = null
            vm.playerPickedPositions.value!!.clear()
            recyclerMaps.smoothScrollToPosition(vm.playerOrder.keys.toList().indexOf(vm.players.value!![0]))
            recyclerMaps.adapter!!.notifyDataSetChanged()
        }

        buttonConfirm.setOnClickListener {
            vm.endPlayerTurn()
        }
    }

    private fun setObservers() {
        vm.playerOrder.forEach { entry ->
            val player = entry.key
            // Called each time a card has been played
            player.observe(viewLifecycleOwner, Observer {
                val playerIndex = vm.playerOrder.keys.toList().indexOf(player)
                Timber.d("Obs: $it updated. Refreshing UI (field #$playerIndex).")
                recyclerMaps.adapter!!.notifyDataSetChanged()
            })
        }

        // Player finished his turn (players.cycle() finished)
        vm.players.observe(viewLifecycleOwner, Observer {
            Timber.d("Obs: Player changed. ${vm.players.value!![0]}'s turn begins")
            clHeader.updatePlayers(vm.players.value!!)
            // Show current player map
            recyclerMaps.smoothScrollToPosition(vm.playerOrder.keys.toList().indexOf(vm.players.value!![0]))
        })

        // Called each time the choice deck has been refreshed
        vm.choice.observe(viewLifecycleOwner, Observer {
            Timber.d("Obs: New choice drawn: choice=${vm.choice.value!!}")
            updateCardHighlighting()
            recyclerChoice.adapter!!.notifyDataSetChanged()
        })

        // Called each time a card in the choice draw has been selected
        vm.playerCardSelection.observe(viewLifecycleOwner, Observer {
            Timber.d("Obs: New card picked. pick=${vm.playerCardSelection.value}")
            updateCardHighlighting()
        })

        // Called each time player registers a position
        vm.playerPickedPositions.observe(viewLifecycleOwner, Observer {
            Timber.d("Obs: New position picked. pick=${vm.playerPickedPositions.value}")
        })
    }


    fun updateCardHighlighting() {
        val card = vm.playerCardSelection.value
        Timber.d("Updating highlighting of choice. Current choice state: ${vm.choice.value!!}, selectedCard=$card")
        if(card == null){
            for (i in 0..3) {
                if(vm.choice.value!!.entries.toList()[i].value) {
                    highlightViewHolderAt(i)
                } else {
                    darkenViewHolderAt(i)
                }
            }
        } else {
            for (i in 0..3) {
                if (i == vm.choice.value!!.keys.indexOf(card)) { // highlight selection
                    highlightViewHolderAt(i)
                } else { // darken other cards
                    darkenViewHolderAt(i)
                }
            }
        }
    }

    private fun highlightViewHolderAt(i: Int) {
        val holder = (recyclerChoice.findViewHolderForAdapterPosition(i) ?: return) as CardChoiceAdapter.ViewHolder
        ((holder.itemView.parent as RecyclerView)
                .findViewHolderForAdapterPosition(i) as CardChoiceAdapter.ViewHolder)
                .imgOverlay.background = null
        Timber.v("Success highlighting ViewHolder #$i")
    }

    private fun darkenViewHolderAt(i: Int) {
        val holder = (recyclerChoice.findViewHolderForAdapterPosition(i) ?: return) as CardChoiceAdapter.ViewHolder
        ((holder.itemView.parent as RecyclerView)
                .findViewHolderForAdapterPosition(i) as CardChoiceAdapter.ViewHolder)
                .imgOverlay.setBackgroundColor(
                        ResourcesCompat.getColor(
                                holder.itemView.resources,
                                R.color.unselected,
                                null
                        )
                )
        Timber.v("Success darkening ViewHolder #$i")
    }


    /**
     * Initialising Views, attaching adapters to RecyclerViews
     */
    private fun initViews(inflater: LayoutInflater, container: ViewGroup?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerChoice = root.findViewById(R.id.recycler_card_choice)
        recyclerMaps = root.findViewById(R.id.recycler_player_field)
        clHeader = root.findViewById(R.id.cl_player_info)
        buttonConfirm = root.findViewById(R.id.button_confirm)
        buttonCancel = root.findViewById(R.id.button_cancel)

        recyclerChoice.setHasFixedSize(true)
        recyclerChoice.adapter = CardChoiceAdapter(vm)
        val recyclerLayout = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerChoice.layoutManager = recyclerLayout
        recyclerChoice.addItemDecoration(RecyclerViewMargin(Utils.pxToDp(2, requireContext()),4))

        recyclerMaps.setHasFixedSize(true)
        recyclerMaps.adapter = PlayerMapAdapter(vm)
        val recyclerLayout2 = AdjustableScrollSpeedLinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false, 125f)
        recyclerLayout2.orientation = LinearLayoutManager.HORIZONTAL
        recyclerMaps.layoutManager = recyclerLayout2
        PagerSnapHelper().attachToRecyclerView(recyclerMaps)
        recyclerMaps.addItemDecoration(RecyclerDotIndicator(0xFFFFFFFFFF.toInt(), 0x66FFFFFF))

        clHeader.updatePlayers(vm.players.value!!)


        return root
    }
}