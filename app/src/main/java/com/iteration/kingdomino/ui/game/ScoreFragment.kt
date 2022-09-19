package com.iteration.kingdomino.ui.game

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.components.RecyclerViewMargin
import com.iteration.kingdomino.components.Utils
import com.iteration.kingdomino.databinding.FragmentScoreBinding
import com.iteration.kingdomino.game.Player
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScoreFragment(private val date: LocalDateTime, private val gameMode: List<String>, private val players: List<Pair<Player, Int>>) : DialogFragment() {

    private lateinit var recyclerPlayers: RecyclerView
    private var _binding: FragmentScoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getTitle(date, gameMode))
            .setView(initViews(layoutInflater, null))
            .create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initViews(inflater, container)
    }

    private fun initViews(inflater: LayoutInflater, container: ViewGroup?): View? {
        val root = inflater.inflate(R.layout.fragment_score, container, false)
        recyclerPlayers = root.findViewById(R.id.recycler_score_players)

        setAdapter(players)
        return root
    }

    private fun getTitle(date: LocalDateTime, gameMode: List<String>) : String {
        val formattedDate = date.format(DateTimeFormatter.ISO_DATE) // TODO: implement format
        val modifiers = if(gameMode.isEmpty()) "classic" else gameMode.joinToString(", ")
        return resources.getString(R.string.score_title, formattedDate, modifiers)
    }

    private fun setAdapter(players: List<Pair<Player, Int>>) {
        recyclerPlayers.setHasFixedSize(true)
        recyclerPlayers.adapter = ScoreAdapter(players)
        recyclerPlayers.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerPlayers.addItemDecoration(RecyclerViewMargin(Utils.pxToDp(2, requireContext()),4))
    }

    companion object {
        const val TAG = "ScoreFragmentDialog"
    }
}