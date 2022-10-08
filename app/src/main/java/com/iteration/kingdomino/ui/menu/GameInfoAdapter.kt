package com.iteration.kingdomino.ui.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.findFragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.databinding.ViewholderGameInfoBinding
import com.iteration.kingdomino.game.model.GameInfo

class GameInfoAdapter(val gameInfos: List<GameInfo>) : RecyclerView.Adapter<GameInfoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewholderGameInfoBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gameInfo = gameInfos[position]

        holder.binding.gameInfoSeed.editText!!.setText(gameInfo.seed.toString())
        holder.binding.gameInfoUID.editText!!.setText(gameInfo.gameId.toString())
        // TODO Chip?

        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Loading game ${gameInfo.gameId}", Toast.LENGTH_SHORT).show()
            val action = LoadGameBottomSheetDirections.actionMenuToGame(gameInfo.gameId)
            findNavController(holder.itemView.findFragment()).navigate(action)
        }
    }

    override fun getItemCount(): Int = gameInfos.size

    class ViewHolder(val binding: ViewholderGameInfoBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}
