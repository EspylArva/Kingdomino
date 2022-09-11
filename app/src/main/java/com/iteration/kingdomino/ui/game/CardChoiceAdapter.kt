package com.iteration.kingdomino.ui.game

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.game.Card
import com.iteration.kingdomino.game.Tile
import timber.log.Timber

class CardChoiceAdapter(private val vm : GameViewModel) : RecyclerView.Adapter<CardChoiceAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.viewholder_card_choice, parent, false))
    }

    override fun getItemCount(): Int {
        return vm.choice.value!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Timber.v("onBindViewHolder of CardChoiceAdapter")
        val card = vm.choice.value!!.entries.toList()[position].key

        holder.lblId.text = card.id.toString()
        holder.setDrawables(card)
        holder.setSize()

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
                    Toast.makeText(holder.itemView.context, holder.itemView.context.getString(R.string.error_select_card, card.id), Toast.LENGTH_SHORT).show()
                }
            } else { // Card chosen is the same
                vm.playerCardSelection.value = null
            }
        }
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

        /**
         * Sets the [ViewHolder] drawables according to the given card.
         * A [ViewHolder] contains two sets of two [ImageView].
         * Each set of [ImageView] represents a [Tile], with its [Tile.Terrain] and [Tile.Crown].
         *
         * @param card the [Card] whose [Tile.Terrain] and [Tile.Crown] will be displayed.
         */
        fun setDrawables(card: Card) {
            imgCrowns1.setBackground(card.tile1.crown.drawableId)
            imgType1.setBackground(card.tile1.type.drawableId)
            imgCrowns2.setBackground(card.tile2.crown.drawableId)
            imgType2.setBackground(card.tile2.type.drawableId)
        }

        /**
         * Sets the [ViewHolder] components to a square shape.
         */
        fun setSize() {
            val metrics = DisplayMetrics()
            itemView.context.display?.getRealMetrics(metrics)
//            WindowManager.getCurrentWindowMetrics()
            val size = (metrics.widthPixels * 0.2).toInt()
            clFirst.layoutParams.width  = size; clFirst.layoutParams.height  = size
            clSecond.layoutParams.width = size; clSecond.layoutParams.height = size
            clSecond.requestLayout()
        }

        /**
         * Sets background for the [ImageView], using given drawable id.
         *
         * @param drawableId the id of the drawable to apply to the [ImageView].
         */
        private fun ImageView.setBackground(drawableId: Int) {
            this.background = if (drawableId != 0) ResourcesCompat.getDrawable(this.resources, drawableId, null) else null
        }
    }

}
