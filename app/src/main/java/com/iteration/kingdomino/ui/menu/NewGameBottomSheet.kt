package com.iteration.kingdomino.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.iteration.kingdomino.R
import com.iteration.kingdomino.components.RecyclerViewMargin
import com.iteration.kingdomino.components.Utils
import com.iteration.kingdomino.databinding.BottomsheetNewGameBinding
import com.iteration.kingdomino.game.model.GameInfo
import com.iteration.kingdomino.game.model.Player
import timber.log.Timber
import java.util.*

class NewGameBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetNewGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomsheetNewGameBinding.inflate(inflater)

        setViews()

        setPlayerRecycler()
        setListeners()
        return binding.root
    }

    private fun setViews() {
        binding.newGameSeed.editText!!.setText(generateNewSeed().toString())
    }

    private fun generateNewSeed(): Int {
        // FIXME: get the list of seeds from DAO
        val seeds = setOf<Int>()
        return generateSequence { (1..65536).random() }.first { !seeds.contains(it) }
    }

    private fun setListeners() {
        binding.newGameConfirmButton.setOnClickListener {
            // Create new game data
            val gameInfo: GameInfo = buildGameInfo()
            Timber.d("Building gameInfo\n$gameInfo")
            // Add it to the SharedPreferences
            // Navigate
            findNavController().navigate(R.id.nav_game) //, gameInfo) //FIXME: pass gameInfo
            this.dismiss()
        }

    }

    private fun setPlayerRecycler() {
        binding.newGamePlayersRecycler.setHasFixedSize(true)
        binding.newGamePlayersRecycler.adapter = PlayerInfoAdapter()
        binding.newGamePlayersRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.newGamePlayersRecycler.addItemDecoration(RecyclerViewMargin(Utils.pxToDp(2, requireContext()), 4))
    }

    private fun buildGameInfo() : GameInfo {
        val players: Collection<Player> = listOf()

        val gameModifiers = binding.modifierChipsContainer.children
            .filter { it is Chip}
            .filter { (it as Chip).isChecked }
            .map { (it as Chip).text.toString() }
            .toSet()

        val seed = binding.newGameSeed.editText!!.text.toString().toInt()
        return GameInfo(gameId = UUID.randomUUID(), players = players, modifiers = gameModifiers, seed = seed)
    }

    companion object {
        const val TAG = "NewGameBottomSheet"
    }
}