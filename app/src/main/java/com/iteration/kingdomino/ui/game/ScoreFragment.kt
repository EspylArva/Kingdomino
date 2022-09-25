package com.iteration.kingdomino.ui.game

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.iteration.kingdomino.R
import com.iteration.kingdomino.components.RecyclerViewMargin
import com.iteration.kingdomino.components.Utils
import com.iteration.kingdomino.databinding.FragmentScoreBinding
import com.iteration.kingdomino.game.model.Player
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScoreFragment(private val date: LocalDateTime, private val gameMode: List<String>, private val players: List<Pair<Player, Int>>) : DialogFragment() {

    private var _binding: FragmentScoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // FIXME: maybe we could just use a RecyclerView and remove fragment_score.xml ?
        // FIXME: Warning: RecyclerView.setMargin(@Px margin) <=== Not dp!
        return AlertDialog.Builder(requireContext())
            .setTitle(getTitle(date, gameMode))
            .setView(initViews(layoutInflater, null))
            .create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initViews(inflater, container)
    }

    // FIXME: Currently required
    private fun initViews(inflater: LayoutInflater, container: ViewGroup?): View? {
        _binding = FragmentScoreBinding.inflate(inflater)
        setAdapter(players)
        return binding.root
    }

    private fun getTitle(date: LocalDateTime, gameMode: List<String>) : String {
        val formattedDate = date.format(DateTimeFormatter.ISO_DATE) // TODO: implement format
        val modifiers = if(gameMode.isEmpty()) "classic" else gameMode.joinToString(", ")
        return resources.getString(R.string.score_title, formattedDate, modifiers)
    }

    private fun setAdapter(players: List<Pair<Player, Int>>) {
        binding.playerScoreInformationRecycler.setHasFixedSize(true)
        binding.playerScoreInformationRecycler.adapter = ScoreAdapter(players)
        binding.playerScoreInformationRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.playerScoreInformationRecycler.addItemDecoration(RecyclerViewMargin(Utils.pxToDp(2, requireContext()),4))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "ScoreFragmentDialog"
    }
}