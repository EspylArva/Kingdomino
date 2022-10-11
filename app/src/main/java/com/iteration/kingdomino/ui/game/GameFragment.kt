package com.iteration.kingdomino.ui.game

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.iteration.kingdomino.R
import com.iteration.kingdomino.components.AdjustableScrollSpeedLinearLayoutManager
import com.iteration.kingdomino.components.RecyclerDotIndicator
import com.iteration.kingdomino.components.RecyclerViewMargin
import com.iteration.kingdomino.components.Utils
import com.iteration.kingdomino.databinding.FragmentGameBinding
import com.iteration.kingdomino.game.data.DaggerProvider
import com.iteration.kingdomino.game.model.GameInfo
import com.iteration.kingdomino.ui.appendix.AppendixFragmentArgs
import com.iteration.kingdomino.ui.settings.SettingsFragment
import timber.log.Timber
import java.time.LocalDateTime
import java.util.stream.Collectors.toList

class GameFragment : Fragment() {

    private lateinit var vm: GameViewModel
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val args: GameFragmentArgs by navArgs()

    private val provider = DaggerProvider.create()

    private fun getGameState() : GameInfo = provider.dataManager().getGameById(requireContext(), args.gameId)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vm = ViewModelProvider(this)[GameViewModel::class.java]
        _binding = FragmentGameBinding.inflate(inflater)

        buildCardChoiceRecycler()
        buildPlayerFieldRecycler()

        // Update player information and order in the [clHeader]
        binding.playerInfoHeader.updatePlayers(vm.playerOrder.keys.toList(), vm.currentPlayer!!)

        // Click listener
        setListeners()
        // Game state observers
        setObservers()

        return binding.root
    }

    /**
     * Setting GUI components listeners.
     */
    private fun setListeners() {
        binding.playerInfoHeader.setOnClickListener {
            vm.deckSize.postValue(0) // FIXME: debugging purposes. Displays to end result.
        }

        binding.cancelChoiceButton.setOnClickListener {
            vm.playerCardSelection.value = null
            vm.playerPickedPositions.value!!.clear()
            binding.playerFieldRecycler.smoothScrollToPosition(vm.playerOrder.keys.toList().indexOf(vm.players.value!![0]))
            binding.playerFieldRecycler.adapter!!.notifyDataSetChanged()
        }

        binding.confirmChoiceButton.setOnClickListener {
            Timber.d("Current settings: ${SettingsFragment.settingsList}")
            if(SettingsFragment.confirmOnPlay){
                val builder = AlertDialog.Builder(context)
                builder.setMessage(context?.getString(R.string.confirmPlayCard, vm.playerCardSelection.value!!.id))
                        .setCancelable(false)
                        .setPositiveButton(resources.getString(R.string.yes)) { _, _ -> vm.endPlayerTurn() }
                        .setNegativeButton(resources.getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
                builder.create().show()
            } else {
                vm.endPlayerTurn()
            }
        }
    }

    /**
     * Setting game states listeners.
     */
    private fun setObservers() {
        vm.playerOrder.forEach { entry ->
            val player = entry.key
            // Called each time a card has been played
            player.observe(viewLifecycleOwner) {
                val playerIndex = vm.playerOrder.keys.toList().indexOf(player)
                Timber.d("Obs: $it updated. Refreshing UI (field #$playerIndex).")
                binding.playerFieldRecycler.adapter!!.notifyDataSetChanged()
            }
        }

        // Player finished his turn (players.cycle() finished)
        vm.players.observe(viewLifecycleOwner) {
            Timber.d("Obs: Player changed. ${vm.players.value!![0]}'s turn begins")
            binding.playerInfoHeader.updatePlayers(vm.playerOrder.keys.toList(), vm.currentPlayer!!)
            // Show current player map
            binding.playerFieldRecycler.smoothScrollToPosition(vm.playerOrder.keys.toList().indexOf(vm.players.value!![0]))
            highlightCurrentPlayerField()
        }

        // Called each time the choice deck has been refreshed
        vm.choice.observe(viewLifecycleOwner) {
            Timber.d("Obs: New choice drawn: choice=${vm.choice.value!!}")
            highlightChoiceCards()
            binding.cardChoiceRecycler.adapter!!.notifyDataSetChanged()
        }

        // Called each time a card in the choice draw has been selected
        vm.playerCardSelection.observe(viewLifecycleOwner, Observer {
            Timber.d("Obs: New card picked. pick=${vm.playerCardSelection.value}")
            highlightChoiceCards()
        })

        // Called each time player registers a position
        vm.playerPickedPositions.observe(viewLifecycleOwner, Observer {
            Timber.d("Obs: New position picked. pick=${vm.playerPickedPositions.value}")
            // Display ghost
            val mapAdapter = binding.playerFieldRecycler.adapter!! as PlayerMapAdapter
            val viewHolder = binding.playerFieldRecycler.findViewHolderForAdapterPosition(vm.currentPlayerIndex) ?: return@Observer
            mapAdapter.showGhost(vm.currentPlayer, viewHolder as PlayerMapAdapter.ViewHolder)

            if(vm.playerCardSelection.value != null){
                val pointDiff = vm.currentPlayer.getPointDiffFor(vm.playerCardSelection.value!!, it)
                binding.playerInfoHeader.showPositionPointDiff(vm.currentPlayerIndex, pointDiff)
            }
        })

        vm.deckSize.observe(viewLifecycleOwner) {
            if(it == 0) {
                val players = vm.playerOrder.keys.stream().map { player -> Pair(player, vm.playerOrder.keys.indexOf(player)) }.collect(toList())
                players.sortByDescending { pair -> pair.first.score }
                ScoreFragment(LocalDateTime.now(), getGameState().modifiers.toList(), players).show(childFragmentManager, ScoreFragment.TAG)
            }
        }
    }

    /**
     * Updates [CardChoiceAdapter] card highlighting.
     * When a card is selected: highlights the selection, and darken all other cards.
     * When no card is selected: highlight available cards, and darken unusable cards.
     */
    private fun highlightChoiceCards() {
        val card = vm.playerCardSelection.value
        Timber.d("Updating highlighting of choice. Current choice state: ${vm.choice.value!!}, selectedCard=$card")

        val selectedCardIndex = vm.choice.value!!.keys.indexOf(card)
        for (i in 0..3) {
            val vh = (binding.cardChoiceRecycler.findViewHolderForAdapterPosition(i) ?: return) as CardChoiceAdapter.ViewHolder
            if(card == null) {
                val cardAtISelectable = vm.choice.value!!.entries.toList()[i].value
                vh.setCardUnselectedHighlighting(cardAtISelectable)
            } else {
                val cardSelected = i == selectedCardIndex
                vh.setCardSelectedHighlighting(cardSelected)
            }
        }
    }

    private fun highlightCurrentPlayerField() {
        binding.playerFieldRecycler.setOnScrollChangeListener { _,_,_,_,_ ->
            for (i in 0 until vm.playerCount) {
                Timber.v("Finding player field #${vm.currentPlayerIndex}: currently showing #$i/${vm.playerCount}")
                val vh = (binding.playerFieldRecycler.findViewHolderForAdapterPosition(i) ?: return@setOnScrollChangeListener) as PlayerMapAdapter.ViewHolder
                if(i == vm.currentPlayerIndex) {
                    vh.itemView.background = ResourcesCompat.getDrawable(resources, R.drawable.golden_glow, null)
                } else {
                    vh.itemView.background = null
                }
            }
        }
    }

    /**
     * Sets up the card choice recycler parameters.
     *
     * - hasFixedSize: whether or not size changes depending on the amount of items
     * - adapter: the content
     * - layoutManager: the orientation
     * - params: [RecyclerViewMargin]: space between each item
     */
    private fun buildCardChoiceRecycler() {
        binding.cardChoiceRecycler.setHasFixedSize(true)
        binding.cardChoiceRecycler.adapter = CardChoiceAdapter(vm)
        binding.cardChoiceRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.cardChoiceRecycler.addItemDecoration(RecyclerViewMargin(Utils.pxToDp(2, requireContext()), 4))
    }

    /**
     * Sets up the player field recycler parameters.
     *
     * - hasFixedSize: whether or not size changes depending on the amount of items
     * - adapter: the content
     * - layoutManager: the orientation. This manager allows for a quicker programmatic scrolling
     * - params:
     *     - [RecyclerDotIndicator]: amount of [RecyclerView.ViewHolder] and position of the currently shown one
     *     - [PagerSnapHelper]: Scrolling incompletely will snap back to the closest [RecyclerView.ViewHolder]
     */
    private fun buildPlayerFieldRecycler() {
        binding.playerFieldRecycler.setHasFixedSize(true)
        binding.playerFieldRecycler.adapter = PlayerMapAdapter(vm)
        binding.playerFieldRecycler.layoutManager = AdjustableScrollSpeedLinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false, 125f)
        PagerSnapHelper().attachToRecyclerView(binding.playerFieldRecycler)
        binding.playerFieldRecycler.addItemDecoration(RecyclerDotIndicator(0xFFFFFFFFFF.toInt(), 0x66FFFFFF))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}