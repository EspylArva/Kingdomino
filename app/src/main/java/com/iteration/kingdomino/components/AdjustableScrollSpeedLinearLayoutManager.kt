package com.iteration.kingdomino.components;

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

public class AdjustableScrollSpeedLinearLayoutManager(var context: Context, var scrollSpeed: Float = 5f) : LinearLayoutManager(context) {

    constructor(context: Context, orientation: Int, reverseLayout: Boolean, scrollSpeed: Float) : this(context, scrollSpeed) {
        this.reverseLayout = reverseLayout
        this.orientation = orientation
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
        val linearSmoothScroller = object:  LinearSmoothScroller(recyclerView.context) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics) : Float = scrollSpeed / displayMetrics.densityDpi
        }
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }
}
