package com.iteration.kingdomino.components

import android.content.Context

class Utils
{
    companion object{
        fun pxToDp(px: Int, context: Context) : Int
        {
            val scale = context.getResources().getDisplayMetrics().density;
            return (px * scale + 0.5f).toInt()
        }
    }
}