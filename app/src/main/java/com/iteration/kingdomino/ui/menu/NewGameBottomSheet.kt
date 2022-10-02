package com.iteration.kingdomino.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.databinding.*
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iteration.kingdomino.R
import com.iteration.kingdomino.components.RecyclerViewMargin
import com.iteration.kingdomino.components.Utils
import com.iteration.kingdomino.databinding.BottomsheetNewGameBinding
import com.iteration.kingdomino.game.model.GameInfo
import com.iteration.kingdomino.game.model.Player
import timber.log.Timber
import java.util.*

class NewGameBottomSheet : BottomSheetDialogFragment(), Observable {

    private var _binding: BottomsheetNewGameBinding? = null
    val binding get() = _binding!!

    val players = ObservableArrayList<Player.Data>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.bottomsheet_new_game, container, false)

        val behaviour = (dialog as BottomSheetDialog).behavior
        behaviour.isFitToContents = false
        behaviour.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
//        behaviour.state = BottomSheetBehavior.STATE_EXPANDED


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
            Timber.d("==> PlayerData size: ${players.size}")
            players.apply { add(Player.Data(generateName()))  }
            Timber.d("<== PlayerData size: ${players.size}")
            binding.newGamePlayersRecycler.adapter!!.notifyItemInserted(players.size - 1)
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
        // FIXME Should probably get the locale here instead of be given as param
        resources.openRawResource(R.raw.names).use {
            val json = it.reader().readLines().joinToString("")
            val typeToken = object : TypeToken<Map<String, List<String>>>() {}.type
            val jObject : Map<String, List<String>> = Gson().fromJson(json, typeToken)
            return jObject[locale]!!.random()
        }
    }

    companion object {
        const val TAG = "NewGameBottomSheet"
    }


    /**
     * From [BaseObservable]. Cannot extend [BaseObservable] because we already extend [BottomSheetDialogFragment].
     */
    @Transient
    private var mCallbacks: PropertyChangeRegistry? = null
    override fun addOnPropertyChangedCallback(callback: OnPropertyChangedCallback) {
        synchronized(this) {
            if (mCallbacks == null) {
                mCallbacks = PropertyChangeRegistry()
            }
        }
        mCallbacks!!.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: OnPropertyChangedCallback) {
        synchronized(this) {
            (mCallbacks ?: return).remove(callback)
        }
    }

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    fun notifyChange() {
        synchronized(this) {
            (mCallbacks ?: return).notifyCallbacks(this, 0, null)
        }
    }

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with [Bindable] to generate a field in
     * `BR` to be used as `fieldId`.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        synchronized(this) {
            (mCallbacks ?: return).notifyCallbacks(this, fieldId, null)
        }
    }
}