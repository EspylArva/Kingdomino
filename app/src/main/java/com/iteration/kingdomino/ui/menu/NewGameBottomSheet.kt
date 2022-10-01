package com.iteration.kingdomino.ui.menu

import android.app.ActionBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.iteration.kingdomino.R
import com.iteration.kingdomino.components.RecyclerViewMargin
import com.iteration.kingdomino.components.Utils
import com.iteration.kingdomino.databinding.BottomsheetNewGameBinding
import com.iteration.kingdomino.databinding.FragmentMenuBinding
import com.iteration.kingdomino.game.model.GameInfo
import com.iteration.kingdomino.game.model.Player
import com.iteration.kingdomino.ui.game.CardChoiceAdapter
import timber.log.Timber

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
            // Add it to the SharedPreferences
            // Navigate
            findNavController().navigate(R.id.nav_game) //, gameInfo) //FIXME: pass gameInfo
            this.dismiss()
        }

        binding.modifierDynastyCheckBox.setOnClickListener {
            binding.modifierDynastyCheckBox.isChecked = true
//            binding.modifierDynastyCheckBox.checkedIconTint =
        }
    }

    private fun setPlayerRecycler() {
        binding.newGamePlayersRecycler.setHasFixedSize(true)
        binding.newGamePlayersRecycler.adapter = PlayerInfoAdapter()
        binding.newGamePlayersRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.newGamePlayersRecycler.addItemDecoration(RecyclerViewMargin(Utils.pxToDp(2, requireContext()), 4))
    }

    private fun buildGameInfo() : GameInfo {
        val players: Collection<Player>

        val seed = binding.newGameSeed.editText!!.text.toString().toInt()
        return GameInfo(gameId = 0, players = listOf(), modifiers = setOf(), seed = seed)
    }

    companion object {
        const val TAG = "NewGameBottomSheet"
    }
}