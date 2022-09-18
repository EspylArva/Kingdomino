package com.iteration.kingdomino.ui.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.components.RecyclerViewMargin
import com.iteration.kingdomino.components.Utils
import com.iteration.kingdomino.game.Player
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ScoreFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScoreFragment(val date: Date, val gameMode: List<String>, val players: List<Player>) : DialogFragment() {
    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null

    private lateinit var lbl_date: TextView
    private lateinit var lbl_gameMode: TextView
    private lateinit var recycler_players: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = initViews(inflater, container)

        setTextViewsContent(date, gameMode)
        setAdapter(players)

        return root
    }

    private fun initViews(inflater: LayoutInflater, container: ViewGroup?): View? {
        val root = inflater.inflate(R.layout.fragment_score, container, false)

        lbl_date = root.findViewById(R.id.lbl_score_date)
        lbl_gameMode = root.findViewById(R.id.lbl_score_game_type)
        recycler_players = root.findViewById(R.id.recycler_score_players)

        return root
    }

    private fun setTextViewsContent(date: Date, gameMode: List<String>) {
        lbl_date.text = date.toString() // TODO: implement format
        val modifiers = if(gameMode.size == 0) "classic" else gameMode.joinToString(", ")
        lbl_gameMode.text = "Game modifier(s): ${modifiers}" // TODO: to resource
    }

    private fun setAdapter(players: List<Player>) {
        recycler_players.adapter = ScoreAdapter(players)
        recycler_players.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recycler_players.addItemDecoration(RecyclerViewMargin(Utils.pxToDp(2, requireContext()),4))
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ScoreFragment.
         */
        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            ScoreFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
    }
}