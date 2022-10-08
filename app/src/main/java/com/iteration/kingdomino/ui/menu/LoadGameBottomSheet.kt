package com.iteration.kingdomino.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.iteration.kingdomino.components.RecyclerViewMargin
import com.iteration.kingdomino.components.Utils
import com.iteration.kingdomino.databinding.BottomsheetLoadGameBinding
import com.iteration.kingdomino.game.data.DaggerProvider
import com.iteration.kingdomino.game.data.DataManager
import timber.log.Timber
import javax.inject.Inject

class LoadGameBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetLoadGameBinding? = null
    val binding get() = _binding!!

    @Inject
    lateinit var dataManager: DataManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomsheetLoadGameBinding.inflate(inflater, container, false)

        dataManager = DaggerProvider.create().dataManager()

        setGameInfoRecycler()
        return binding.root
    }

    private fun setGameInfoRecycler() {

        val startedGames = dataManager.getStartedGames(requireContext())

        Timber.d("Games loaded from shared preferences: $startedGames")

        binding.currentGameRecycler.setHasFixedSize(true)
        binding.currentGameRecycler.adapter = GameInfoAdapter(startedGames)
        binding.currentGameRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.currentGameRecycler.addItemDecoration(RecyclerViewMargin(Utils.pxToDp(2, requireContext()), 4))
    }
}