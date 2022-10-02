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

class PlayerInfoAdapter(val players: MutableList<Player.Data>) : RecyclerView.Adapter<PlayerInfoAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewholderPlayerInfoBinding.inflate(inflater, parent, false)
        return ViewHolder(binding).bindAdapter(this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setListeners()
    }

    override fun getItemCount(): Int = players.size

    class ViewHolder(val binding: ViewholderPlayerInfoBinding) : RecyclerView.ViewHolder(binding.root) {

        private var _adapter: PlayerInfoAdapter? = null
        private val adapter : PlayerInfoAdapter get() = _adapter!!

        init {
            val playerTypes = Player.Type.values().map { it.label } //FIXME: parameterize for language support
            val dropDownAdapter = ArrayAdapter(itemView.context, R.layout.list_item, playerTypes)
            (binding.playerTypeDropdown.editText as? AutoCompleteTextView)?.setAdapter(dropDownAdapter)
        }

        fun setListeners() {
            binding.removePlayerButton.setOnClickListener {
                Timber.d("Removing item at adapterPosition=$adapterPosition")
                adapter.players.removeAt(adapterPosition)
                adapter.notifyItemRemoved(adapterPosition)
            }

            binding.newPlayerName.editText!!.setText(adapter.players[adapterPosition].name)
            binding.newPlayerName.editText!!.addTextChangedListener { adapter.players[adapterPosition].name = it.toString() }
            binding.playerTypeDropdown.editText!!.addTextChangedListener { adapter.players[adapterPosition].type = Player.Type.parse(it.toString()) }
        }

        fun bindAdapter(adapter: PlayerInfoAdapter) : ViewHolder {
            _adapter = adapter
            return this
        }

    }
}