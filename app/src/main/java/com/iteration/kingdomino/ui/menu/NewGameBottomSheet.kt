package com.iteration.kingdomino.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iteration.kingdomino.BR
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
    val binding get() = _binding!!

    val players = mutableListOf<Player.Data>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomsheetNewGameBinding.inflate(inflater)

        setViews()

        setPlayerRecycler()
        setListeners()
        return binding.root
    }

    private fun setViews() {
        binding.newGameSeed.editText!!.setText(generateNewSeed().toString())

        players.add(Player.Data(generateName()))
        players.add(Player.Data(generateName()))
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

        binding.addPlayerButton.setOnClickListener {
            players.add(Player.Data(generateName()))
            binding.newGamePlayersRecycler.adapter!!.notifyItemInserted(players.size-1)
        }


    }

    private fun setPlayerRecycler() {
        binding.newGamePlayersRecycler.setHasFixedSize(true)
        binding.newGamePlayersRecycler.adapter = PlayerInfoAdapter(players)
        binding.newGamePlayersRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.newGamePlayersRecycler.addItemDecoration(RecyclerViewMargin(Utils.pxToDp(2, requireContext()), 4))
    }


    private fun buildGameInfo() : GameInfo {
        val listPlayer = players.map { Player(it) }

        val gameModifiers = binding.modifierChipsContainer.children
            .filter { it is Chip}
            .filter { (it as Chip).isChecked }
            .map { (it as Chip).text.toString() }
            .toSet()

        val seed = binding.newGameSeed.editText!!.text.toString().toInt()
        return GameInfo(gameId = UUID.randomUUID(), players = listPlayer, modifiers = gameModifiers, seed = seed)
    }

    private fun generateNewSeed(): Int {
        // FIXME: get the list of seeds from DAO
        val seeds = setOf<Int>()
        return generateSequence { (1..65536).random() }.first { !seeds.contains(it) }
    }

    private fun generateName(locale: String = "en") : String {
        resources.openRawResource(R.raw.names).use {
            val json = it.reader().readLines().joinToString("")
            val typeToken = object : TypeToken<Map<String, List<String>>>() {}.type
            val jObject : Map<String, List<String>> = Gson().fromJson(json, typeToken)
            Timber.d("Json=$json JsonObject=$jObject")
            return jObject[locale]!!.random()
        }
    }

    companion object {
        const val TAG = "NewGameBottomSheet"
    }
}