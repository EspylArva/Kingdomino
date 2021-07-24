package com.iteration.kingdomino.ui.home

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.game.Card
import timber.log.Timber

class CardChoiceAdapter(private val cards : LiveData<MutableList<Card>>) : RecyclerView.Adapter<CardChoiceAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.viewholder_card_choice, parent, false))
    }

    override fun getItemCount(): Int {
        return cards.value!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setBackground(holder.imgCrowns1, cards.value!![position].tile1.crown.drawableId)
        setBackground(holder.imgType1, cards.value!![position].tile1.type.drawableId)
        setBackground(holder.imgCrowns2, cards.value!![position].tile2.crown.drawableId)
        setBackground(holder.imgType2, cards.value!![position].tile2.type.drawableId)

        val metrics = DisplayMetrics()
        holder.itemView.context.display?.getRealMetrics(metrics)

        val size = (metrics.widthPixels * 0.2).toInt()
        setCardSize(holder, size)

        holder.itemView.setOnClickListener {
            // highlight selection
//            for(i in 0..3)
//            {
//                if(i != position)
//                {
//
//                }
//            }

        }
    }

    private fun setCardSize(holder: ViewHolder, size: Int) {
        holder.clFirst.layoutParams.width  = size; holder.clFirst.layoutParams.height  = size
        holder.clSecond.layoutParams.width = size; holder.clSecond.layoutParams.height = size
        holder.clSecond.requestLayout()
    }

    private fun setBackground(iv : ImageView, drawableId : Int) {
        if (drawableId != 0) {
            iv.background = ResourcesCompat.getDrawable(iv.resources, drawableId, null)
        }
        else
        {
            iv.background = null
        }
    }




    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val clFirst : ConstraintLayout = itemView.findViewById(R.id.card_first_tile)
        val clSecond : ConstraintLayout = itemView.findViewById(R.id.card_second_tile)

        val imgCrowns1 : ImageView = itemView.findViewById(R.id.img_card_crowns_one)
        val imgCrowns2 : ImageView = itemView.findViewById(R.id.img_card_crowns_two)
        val imgType1 : ImageView = itemView.findViewById(R.id.img_card_type_one)
        val imgType2 : ImageView = itemView.findViewById(R.id.img_card_type_two)
    }

}
