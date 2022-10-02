package com.iteration.kingdomino.ui.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.databinding.ViewholderPlayerInfoBinding
import com.iteration.kingdomino.game.model.Player
import timber.log.Timber

class PlayerInfoAdapter(private val players: MutableList<Player.Data>) : RecyclerView.Adapter<PlayerInfoAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewholderPlayerInfoBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.removePlayerButton.setOnClickListener {
            Timber.d("Removing at position=$position (name=${holder.binding.newPlayerName.editText!!.text} type=${holder.binding.playerTypeDropdown.editText!!.text})")
            Timber.d("Before removing: $players")
            players.removeAt(position)
            Timber.d("After  removing: $players")
            notifyDataSetChanged()
        }

        holder.binding.newPlayerName.editText!!.setText(players[position].name)
        holder.binding.newPlayerName.editText!!.addTextChangedListener { players[position].name = it.toString() }
        holder.binding.playerTypeDropdown.editText!!.addTextChangedListener { players[position].type = Player.Type.parse(it.toString()) }
    }

    override fun getItemCount(): Int = players.size

    class ViewHolder(val binding: ViewholderPlayerInfoBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            val playerTypes = Player.Type.values().map { it.label } //FIXME: parameterize for language support
            val adapter = ArrayAdapter(itemView.context, R.layout.list_item, playerTypes)
            (binding.playerTypeDropdown.editText as? AutoCompleteTextView)?.setAdapter(adapter)


        }

    }
}