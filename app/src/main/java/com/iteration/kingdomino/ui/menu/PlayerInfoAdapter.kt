package com.iteration.kingdomino.ui.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.databinding.ViewholderPlayerInfoBinding

class PlayerInfoAdapter : RecyclerView.Adapter<PlayerInfoAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewholderPlayerInfoBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int = 2

    class ViewHolder(val binding: ViewholderPlayerInfoBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            val playerTypes = listOf("Human", "AI") //FIXME: parameterize for language support
            val adapter = ArrayAdapter(itemView.context, R.layout.list_item, playerTypes)
            (binding.playerTypeDropdown.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        }

    }
}