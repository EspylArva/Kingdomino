package com.iteration.kingdomino.components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.iteration.kingdomino.R

class GameHeader @JvmOverloads constructor(
        ctx : Context,
        attrs : AttributeSet? = null,
        defStyleAttr : Int = 0
) : LinearLayout(ctx, attrs, defStyleAttr)
{
    val listPlayerInfo : MutableList<Pair<TextView, TextView>> = mutableListOf()
    private val listLayouts : MutableList<LinearLayout> = mutableListOf()



    init {
        this.orientation = HORIZONTAL
        this.gravity = Gravity.CENTER

        for(i in 0..3)
        {
            val linearLayout = LinearLayout(ctx)
            val tvPlayerName = TextView(ctx)
            val tvPlayerScore = TextView(ctx)

            linearLayout.addView(tvPlayerName)
            linearLayout.addView(tvPlayerScore)

            linearLayout.orientation = VERTICAL
            linearLayout.gravity = Gravity.CENTER
            linearLayout.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f)

            tvPlayerName.text = "Player $i"
            tvPlayerScore.text = ctx.getString(R.string.player_score, 0)

            listPlayerInfo.add(Pair(tvPlayerName, tvPlayerScore))
            listLayouts.add(linearLayout)
        }

        val iterator = listLayouts.iterator()
        while(iterator.hasNext())
        {

            this.addView(iterator.next())
            if(iterator.hasNext())
            {
                val ivSeparator = ImageView(ctx)
                ivSeparator.background = ResourcesCompat.getDrawable(ctx.resources, R.drawable.ic_baseline_arrow_forward_ios_24, null)
                this.addView(ivSeparator)
            }
        }
    }

}