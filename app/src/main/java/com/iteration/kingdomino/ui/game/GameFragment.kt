package com.iteration.kingdomino.ui.home

import android.app.AlertDialog
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

    /**
     * Drawn cards RecyclerView.
     * Should always contain four cards, and display both the card tiles and the card index.
     */
    private lateinit var recyclerChoice : RecyclerView

    /**
     * Player field RecyclerView.
     * Should contain as many panels as there are players.
     */
    private lateinit var recyclerMaps : RecyclerView

    /**
     * Header where player information is displayed.
     * Should display players in order, as well as their points.
     * When a card is placed but not played, the point differential should be displayed.
     */
    private lateinit var clHeader : GameHeader

    /**
     * Button to reset card pick and position selection.
     * Clicking the cancel button should display the current player's field.
     */
    private lateinit var buttonCancel : Button

    /**
     * Button to confirm playing the card.
     * Any card placed but not confirmed will not be played: it will be displayed as "ghost" tiles, and additional points will be hinted.
     */
    private lateinit var buttonConfirm : Button

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        vm = ViewModelProvider(this).get(GameViewModel::class.java)
        val root = initViews(inflater, container)
        // Click listener
        setListeners()
        // Game state observers
        setObservers()
        return root
    }

    /**
     * Setting GUI components listeners.
     */
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
            val builder = AlertDialog.Builder(context)
            builder.setMessage(context?.getString(R.string.confirmPlayCard, vm.playerCardSelection.value!!.id))
                    .setCancelable(false)
                    .setPositiveButton("Yes") { _, _ -> vm.endPlayerTurn() }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            builder.create().show()
        }
    }

    /**
     * Setting game states listeners.
     */
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
            clHeader.updatePlayers(vm.playerOrder.keys.toList(), vm.currentPlayer!!)
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
            // Display ghost
            val mapAdapter = recyclerMaps.adapter!! as PlayerMapAdapter
            val viewHolder = recyclerMaps.findViewHolderForAdapterPosition(vm.currentPlayerIndex) ?: return@Observer
            mapAdapter.showGhost(vm.currentPlayer!!, viewHolder as PlayerMapAdapter.ViewHolder)
        })
    }

    /**
     * Updates [CardChoiceAdapter] card highlighting.
     * When a card is selected: highlights the selection, and darken all other cards.
     * When no card is selected: highlight available cards, and darken unusable cards.
     */
    private fun updateCardHighlighting() {
        val card = vm.playerCardSelection.value
        Timber.d("Updating highlighting of choice. Current choice state: ${vm.choice.value!!}, selectedCard=$card")

        for (i in 0..3) {
            val highlightCard = if(card == null){
                // No card selected AND
                // Card has not been played yet (thus is available)
                vm.choice.value!!.entries.toList()[i].value
            } else {
                // A card has been selected AND1
                // Card is currently selected
                i == vm.choice.value!!.keys.indexOf(card)
            }

            if(highlightCard) {
                highlightViewHolderAt(i)
            } else {
                darkenViewHolderAt(i)
            }

        }
    }

    /**
     * Highlight [CardChoiceAdapter.ViewHolder] at given index.
     *
     * @param i card index in the card choice list. This parameter should always be between 0 and 3.
     */
    private fun highlightViewHolderAt(i: Int) {
        val holder = (recyclerChoice.findViewHolderForAdapterPosition(i) ?: return) as CardChoiceAdapter.ViewHolder
        ((holder.itemView.parent as RecyclerView)
                .findViewHolderForAdapterPosition(i) as CardChoiceAdapter.ViewHolder)
                .imgOverlay.background = null
        Timber.v("Success highlighting ViewHolder #$i")
    }

    /**
     * Darkens [CardChoiceAdapter.ViewHolder] at given index.
     *
     * @param i card index in the card choice list. This parameter should always be between 0 and 3.
     */
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
     * Initialises Views, attaching Adapters to RecyclerViews
     *
     * @param inflater the LayoutInflater
     * @param container the ViewGroup
     * @return the root view
     */
    private fun initViews(inflater: LayoutInflater, container: ViewGroup?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerChoice = root.findViewById(R.id.recycler_card_choice)
        recyclerMaps = root.findViewById(R.id.recycler_player_field)
        clHeader = root.findViewById(R.id.cl_player_info)
        buttonConfirm = root.findViewById(R.id.button_confirm)
        buttonCancel = root.findViewById(R.id.button_cancel)

        // Sets up [recyclerChoice] parameters
        recyclerChoice.setHasFixedSize(true)
        recyclerChoice.adapter = CardChoiceAdapter(vm)
        recyclerChoice.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerChoice.addItemDecoration(RecyclerViewMargin(Utils.pxToDp(2, requireContext()),4))

        // Sets up [recyclerMaps] parameters
        recyclerMaps.setHasFixedSize(true)
        recyclerMaps.adapter = PlayerMapAdapter(vm)
        recyclerMaps.layoutManager = AdjustableScrollSpeedLinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false, 125f)
        PagerSnapHelper().attachToRecyclerView(recyclerMaps)
        recyclerMaps.addItemDecoration(RecyclerDotIndicator(0xFFFFFFFFFF.toInt(), 0x66FFFFFF))

        // Update player information and order in the [clHeader]
        clHeader.updatePlayers(vm.playerOrder.keys.toList(), vm.currentPlayer!!)

        return root
    }
}