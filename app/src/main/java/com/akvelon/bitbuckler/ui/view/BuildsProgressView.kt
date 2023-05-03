/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 19 February 2021
 */

package com.akvelon.bitbuckler.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.extension.getColor
import com.akvelon.bitbuckler.extension.getDimension
import com.akvelon.bitbuckler.extension.hide
import com.akvelon.bitbuckler.extension.show

class BuildsProgressView(
    context: Context,
    attrs: AttributeSet?
) : View(context, attrs) {

    private var successfulCount: Int
    private var failedCount: Int
    private var inProgressCount: Int
    private var stoppedCount: Int
    private val buildsCount
        get() = successfulCount + failedCount + inProgressCount + stoppedCount

    init {
        context.obtainStyledAttributes(attrs, R.styleable.BuildsProgressView).run {
            successfulCount = getInt(R.styleable.BuildsProgressView_successful_count, 0)
            failedCount = getInt(R.styleable.BuildsProgressView_failed_count, 0)
            inProgressCount = getInt(R.styleable.BuildsProgressView_in_progress_count, 0)
            stoppedCount = getInt(R.styleable.BuildsProgressView_stopped_count, 0)
            recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (buildsCount != 0) {
            drawBuilds(canvas)
            show()
        } else {
            hide()
        }
    }

    fun setCounts(successful: Int, failed: Int, inProgress: Int, stopped: Int) {
        successfulCount = successful
        failedCount = failed
        inProgressCount = inProgress
        stoppedCount = stopped

        invalidate()
    }

    private fun drawBuilds(canvas: Canvas) {
        val successfulWidth = successfulCount / buildsCount.toFloat() * width
        val failedWidth = failedCount / buildsCount.toFloat() * width + successfulWidth
        val stoppedWidth = stoppedCount / buildsCount.toFloat() * width + failedWidth
        val inProgressWidth = inProgressCount / buildsCount.toFloat() * width + stoppedWidth

        drawBuild(canvas, 0F, successfulWidth, R.color.greenDarkest)
        drawBuild(canvas, successfulWidth, failedWidth, R.color.redDark)
        drawBuild(canvas, failedWidth, stoppedWidth, R.color.orange)
        drawBuild(canvas, stoppedWidth, inProgressWidth, R.color.grayLight)
    }

    private fun drawBuild(canvas: Canvas, start: Float, end: Float, @ColorRes colorId: Int) {
        canvas.drawRoundRect(
            start,
            0F,
            end,
            height.toFloat(),
            getDimension(R.dimen.radiusTiny),
            getDimension(R.dimen.radiusTiny),
            getPaint(colorId)
        )
    }

    private fun getPaint(@ColorRes resId: Int) = Paint().apply {
        color = getColor(resId)
        style = Paint.Style.FILL
    }
}
