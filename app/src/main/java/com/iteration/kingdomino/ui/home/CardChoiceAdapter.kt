package com.iteration.kingdomino.ui.home

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.findFragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.game.Card
import timber.log.Timber
import java.lang.Exception

class CardChoiceAdapter(private val vm : GameViewModel) : RecyclerView.Adapter<CardChoiceAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.viewholder_card_choice, parent, false))
    }

    override fun getItemCount(): Int {
        return vm.choice.value!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Timber.d("onBindViewHolder of CardChoiceAdapter")
        val card = vm.choice.value!!.entries.toList()[position].key

        holder.lblId.text = card.id.toString()

        holder.imgCrowns1.setBackground(card.tile1.crown.drawableId)
        holder.imgType1.setBackground(card.tile1.type.drawableId)
        holder.imgCrowns2.setBackground(card.tile2.crown.drawableId)
        holder.imgType2.setBackground(card.tile2.type.drawableId)

        setCardSize(holder)

        holder.itemView.setOnClickListener {
            Timber.d(
            """
                |Player chooses a card
                |Card: position=$position selection=${card}
                |Previously selected card: selection=${vm.playerCardSelection.value}
            """.trimMargin()
            )

            // (No card was chosen) OR
            // (A card was chosen AND Chosen card is different)
            if (vm.playerCardSelection.value == null || (vm.playerCardSelection.value != card)) {
                if (vm.choice.value!![card] == true) { // Card can be played (has not been played yet)
                    vm.playerCardSelection.value = card
                } else {
                    Timber.w("Card $card has already been played by another player this turn (registeredState=${vm.choice.value!![card]})")
                }
            } else { // Card chosen is the same
                vm.playerCardSelection.value = null
            }
            Timber.d("Currently selected card=${vm.playerCardSelection.value}")
        }
    }

    private fun setCardSize(holder: ViewHolder) {
        val metrics = DisplayMetrics()
        holder.itemView.context.display?.getRealMetrics(metrics)
        val size = (metrics.widthPixels * 0.2).toInt()
        holder.clFirst.layoutParams.width  = size; holder.clFirst.layoutParams.height  = size
        holder.clSecond.layoutParams.width = size; holder.clSecond.layoutParams.height = size
        holder.clSecond.requestLayout()
    }


    private fun ImageView.setBackground(drawableId: Int) {
        this.background = if (drawableId != 0) ResourcesCompat.getDrawable(this.resources, drawableId, null) else null
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val lblId : TextView = itemView.findViewById(R.id.lbl_card_id)

        val clFirst : ConstraintLayout = itemView.findViewById(R.id.card_first_tile)
        val clSecond : ConstraintLayout = itemView.findViewById(R.id.card_second_tile)

        val imgCrowns1 : ImageView = itemView.findViewById(R.id.img_card_crowns_one)
        val imgCrowns2 : ImageView = itemView.findViewById(R.id.img_card_crowns_two)
        val imgType1 : ImageView = itemView.findViewById(R.id.img_card_type_one)
        val imgType2 : ImageView = itemView.findViewById(R.id.img_card_type_two)

        val imgOverlay : ImageView = itemView.findViewById(R.id.img_card_overlay)
    }

}
