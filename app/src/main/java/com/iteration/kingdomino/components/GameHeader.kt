package com.iteration.kingdomino.components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.iteration.kingdomino.R
import com.iteration.kingdomino.game.Player

class GameHeader @JvmOverloads constructor(var ctx : Context, attrs : AttributeSet? = null, defStyleAttr : Int = 0)
    : LinearLayout(ctx, attrs, defStyleAttr) {

    /**
     * TextViews containing information on player.
     * first:  Player's ID
     * second: Player's score
     */
    private val listPlayerInfo : MutableList<Pair<TextView, TextView>> = mutableListOf()
    private val listLayouts : MutableList<LinearLayout> = mutableListOf()

    init {
        this.orientation = HORIZONTAL
        this.gravity = Gravity.CENTER
    }

    private fun addSep() {
        val iterator = listLayouts.iterator()
        while(iterator.hasNext()) {
            val view = iterator.next()
//            if (view.parent != null) {
//                (view.parent as ViewGroup).removeView(view)
//            }
            this.addView(view)
            if(iterator.hasNext())
            {
                val ivSeparator = ImageView(ctx)
                ivSeparator.background = ResourcesCompat.getDrawable(ctx.resources, R.drawable.ic_baseline_arrow_forward_ios_24, null)
                this.addView(ivSeparator)
            }
        }


    }

    fun updatePlayers(players: List<Player>) {
        this.removeAllViews()
        listLayouts.clear()
        listPlayerInfo.clear()

        for(i in players.indices)
        {
            val linearLayout = LinearLayout(ctx)
            val tvPlayerName = TextView(ctx)
            val tvPlayerScore = TextView(ctx)

            linearLayout.addView(tvPlayerName)
            linearLayout.addView(tvPlayerScore)

            linearLayout.orientation = VERTICAL
            linearLayout.gravity = Gravity.CENTER
            linearLayout.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f)

            tvPlayerName.text = players[i].name
            tvPlayerScore.text = resources.getString(R.string.player_score, players[i].getScore())

            listPlayerInfo.add(Pair(tvPlayerName, tvPlayerScore))
            listLayouts.add(linearLayout)
        }

        addSep()
    }
}