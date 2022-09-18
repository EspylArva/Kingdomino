package com.iteration.kingdomino.ui.game

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.game.Player

class ScoreAdapter(val players: List<Player>) : RecyclerView.Adapter<ScoreAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.viewholder_score_player, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount() = players.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {

    }
}