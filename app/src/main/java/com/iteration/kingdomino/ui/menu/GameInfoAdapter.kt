package com.iteration.kingdomino.ui.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.findFragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
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
        holder.binding.gameInfoCreatedDate.editText!!.setText(gameInfo.creationDate.toString())
        holder.binding.gameInfoUpdatedDate.editText!!.setText(gameInfo.lastUpdateDate.toString())

        // TODO Chip?
        holder.setChips(gameInfo.modifiers)

        holder.binding.gameInfoFilter.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Loading game ${gameInfo.gameId}", Toast.LENGTH_SHORT).show()
            val action = LoadGameBottomSheetDirections.actionMenuToGame(gameInfo.gameId)
            findNavController(holder.itemView.findFragment()).navigate(action)
        }
    }

    override fun getItemCount(): Int = gameInfos.size

    class ViewHolder(val binding: ViewholderGameInfoBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.modifierChipsContainer.children.forEach { it.isClickable = false }
        }

        fun setChips(modifiers: Set<String>) {
            binding.modifierChipsContainer.children
                .filter { it is Chip }
                .filter { modifiers.contains((it as Chip).text) }
                .forEach { (it as Chip).isChecked = true }
        }

    }
}
