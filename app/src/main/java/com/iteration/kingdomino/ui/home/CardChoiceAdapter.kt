package com.iteration.kingdomino.ui.home

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.game.Card
import timber.log.Timber

class CardChoiceAdapter(private val cards : List<Card>) : RecyclerView.Adapter<CardChoiceAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardChoiceAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.card_choice, parent, false))
    }

    override fun getItemCount(): Int {
        return cards.size;
    }

    override fun onBindViewHolder(holder: CardChoiceAdapter.ViewHolder, position: Int) {
//        TODO("Not yet implemented")
        //

        Timber.d(cards[position].toString())

        setBackground(holder.imgCrowns1, cards[position].tile1.crown.drawableId)
        setBackground(holder.imgType1, cards[position].tile1.type.drawableId)
        setBackground(holder.imgCrowns2, cards[position].tile2.crown.drawableId)
        setBackground(holder.imgType2, cards[position].tile2.type.drawableId)

        val metrics = DisplayMetrics()
        holder.itemView.context.display?.getRealMetrics(metrics)

        val size = (metrics.widthPixels * 0.2).toInt()
        Timber.d("Metrics: ${metrics.heightPixels} -- Tile size: $size")
        setCardSize(holder, size)

        holder.imgCrowns1.setOnClickListener { Timber.d("Touched ${cards[position].tile1.type} holding ${cards[position].tile1.crown} crowns") }
        holder.imgType1.setOnClickListener { Timber.d("Touched ${cards[position].tile1.type} holding ${cards[position].tile1.crown} crowns") }

        holder.imgCrowns2.setOnClickListener { Timber.d("Touched ${cards[position].tile2.type} holding ${cards[position].tile2.crown} crowns") }
        holder.imgType2.setOnClickListener { Timber.d("Touched ${cards[position].tile2.type} holding ${cards[position].tile2.crown} crowns") }

    }

    private fun setCardSize(holder: ViewHolder, size: Int) {
        holder.imgCrowns1.layoutParams.width = size
        holder.imgCrowns1.requestLayout()
        holder.imgCrowns1.layoutParams.height = size
        holder.imgCrowns1.requestLayout()

        holder.imgType1.layoutParams.width = size
        holder.imgType1.requestLayout()
        holder.imgType1.layoutParams.height = size
        holder.imgType1.requestLayout()

        holder.imgCrowns2.layoutParams.width = size
        holder.imgCrowns2.requestLayout()
        holder.imgCrowns2.layoutParams.height = size
        holder.imgCrowns2.requestLayout()

        holder.imgType2.layoutParams.width = size
        holder.imgType2.requestLayout()
        holder.imgType2.layoutParams.height = size
        holder.imgType2.requestLayout()
    }

    private fun setBackground(iv : ImageView, drawableId : Int) {
        if (drawableId != 0) {
            iv.background = ResourcesCompat.getDrawable(iv.resources, drawableId, null)
        }
    }




    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val imgCrowns1 : ImageView = itemView.findViewById(R.id.img_card_crowns_one)
        val imgCrowns2 : ImageView = itemView.findViewById(R.id.img_card_crowns_two)
        val imgType1 : ImageView = itemView.findViewById(R.id.img_card_type_one)
        val imgType2 : ImageView = itemView.findViewById(R.id.img_card_type_two)
    }

}
