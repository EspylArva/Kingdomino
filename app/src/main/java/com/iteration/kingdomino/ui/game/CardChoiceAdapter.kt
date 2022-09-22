package com.iteration.kingdomino.ui.game

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IntRange
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.databinding.ViewholderCardChoiceBinding
import com.iteration.kingdomino.game.Card
import com.iteration.kingdomino.game.Tile
import timber.log.Timber

class CardChoiceAdapter(private val vm : GameViewModel) : RecyclerView.Adapter<CardChoiceAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewholderCardChoiceBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return vm.choice.value!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Timber.v("onBindViewHolder of CardChoiceAdapter")
        val card = vm.choice.value!!.entries.toList()[position].key

        holder.binding.cardIdLabel.text = card.id.toString()
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



    class ViewHolder(val binding: ViewholderCardChoiceBinding) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Sets the [ViewHolder] drawables according to the given card.
         * A [ViewHolder] contains two sets of two [ImageView].
         * Each set of [ImageView] represents a [Tile], with its [Tile.Terrain] and [Tile.Crown].
         *
         * @param card the [Card] whose [Tile.Terrain] and [Tile.Crown] will be displayed.
         */
        fun setDrawables(card: Card) {
            binding.tileOneCrownImageView.setBackground(card.tile1.crown.drawableId)
            binding.tileOneTerrainImageView.setBackground(card.tile1.type.drawableId)
            binding.tileTwoCrownImageView.setBackground(card.tile2.crown.drawableId)
            binding.tileTwoTerrainImageView.setBackground(card.tile2.type.drawableId)
        }

        /**
         * Sets the [ViewHolder] components to a square shape.
         */
        fun setSize() {
            val metrics = itemView.context.getSystemService(WindowManager::class.java).currentWindowMetrics.bounds
            val size = (metrics.width() * 0.2).toInt()
            binding.tileOneContainer.layoutParams.width  = size; binding.tileOneContainer.layoutParams.height  = size
            binding.tileTwoContainer.layoutParams.width = size; binding.tileTwoContainer.layoutParams.height = size
            binding.tileTwoContainer.requestLayout()
        }

        /**
         * Sets background for the [ImageView], using given drawable id.
         *
         * @param drawableId the id of the drawable to apply to the [ImageView].
         */
        private fun ImageView.setBackground(drawableId: Int) {
            this.background = if (drawableId != 0) ResourcesCompat.getDrawable(this.resources, drawableId, null) else null
        }


        /**
         * Updates [CardChoiceAdapter] card highlighting.
         * When a card is selected: highlights the selection, and darken all other cards.
         * When no card is selected: highlight available cards, and darken unusable cards.
         */
        fun setCardUnselectedHighlighting(cardSelectable: Boolean) {
            itemView.background = null
            if (cardSelectable) {
                binding.shadowOverlayImageView.background = null
            } else {
                binding.shadowOverlayImageView.setBackgroundColor(
                    ResourcesCompat.getColor(
                        itemView.resources,
                        R.color.unselected,
                        null
                    )
                )
            }
        }

        fun setCardSelectedHighlighting(cardSelected: Boolean) {
            itemView.background = null
            if (cardSelected) {
                binding.shadowOverlayImageView.background = null
                itemView.background = ResourcesCompat.getDrawable(itemView.resources, R.drawable.golden_glow, null)
            } else {
                binding.shadowOverlayImageView.setBackgroundColor(
                    ResourcesCompat.getColor(
                        itemView.resources,
                        R.color.unselected,
                        null
                    )
                )
            }
        }
    }

}
