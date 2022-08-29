package com.iteration.kingdomino.components

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerDotIndicator(@ColorInt private val colorActive : Int, @ColorInt private val colorInactive : Int) : RecyclerView.ItemDecoration() {
    private val dp = Resources.getSystem().displayMetrics.density
    private val indicatorHeight = (dp*16).toInt()
    private val indicatorStrokeWidth = (dp*4)
    private val indicatorItemLength = (dp*4)
    private val indicatorItemPadding = (dp*8)
    private val interpolator = AccelerateDecelerateInterpolator()
    private val paint = Paint()

    init {
        paint.strokeWidth = indicatorStrokeWidth
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val itemCount = parent.adapter?.itemCount

        val totalLength = indicatorItemLength * itemCount!!
        val paddingBetweenItems = 0.coerceAtLeast(itemCount - 1) * indicatorItemPadding
        val indicatorTotalWidth = totalLength + paddingBetweenItems
        val indicatorStartX = (parent.width - indicatorTotalWidth) / 2F
        val indicatorPosY = parent.height - indicatorHeight /2F

        drawInactiveIndicators(c, indicatorStartX, indicatorPosY, itemCount)

        val layoutManager = parent.layoutManager as LinearLayoutManager
        val activePosition = layoutManager.findFirstVisibleItemPosition()
        if(activePosition == RecyclerView.NO_POSITION) { return }

        val activeChild = layoutManager.findViewByPosition(activePosition)
        val left = activeChild?.left; val width = activeChild?.width

        val progress = interpolator.getInterpolation(
                (left?.times(-1) ?: 0).div(width?.toFloat() ?: 1f)
        )
        drawHighlights(c, indicatorStartX, indicatorPosY, activePosition, progress, itemCount)
    }


    private fun drawInactiveIndicators(c: Canvas, indicatorStartX: Float, indicatorPosY: Float, itemCount: Int) {
        paint.color = colorInactive
        val itemWidth = indicatorItemLength + indicatorItemPadding
        var start = indicatorStartX
        for(i in 0 until itemCount) {
            c.drawCircle(start, indicatorPosY, indicatorItemLength/2F, paint)
            start += itemWidth
        }

    }

    private fun drawHighlights(c: Canvas, indicatorStartX: Float, indicatorPosY: Float, activePosition: Int, progress: Float, itemCount: Int) {
        paint.color = colorActive
        val itemWidth = indicatorItemLength + indicatorItemPadding
        val highlightStart = indicatorStartX + itemWidth * activePosition
        if( progress == 0F)
        {
            c.drawCircle(highlightStart, indicatorPosY, indicatorItemLength/2F, paint)
        }
        else
        {
            val partialLength = indicatorItemLength * progress
            c.drawCircle(highlightStart + partialLength, indicatorPosY, indicatorItemLength/2F, paint)
        }

    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = indicatorHeight
    }
}