/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 02 June 2021
 */

package com.akvelon.bitbuckler.ui.span

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan

class RoundedBackgroundSpan(
    private val backgroundColor: Int,
    private val textColor: Int
) :
    ReplacementSpan() {

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ) = (DRAW_PADDING * 2 + paint.measureText(text, start, end)).toInt()

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        paint.color = backgroundColor

        val width = paint.measureText(text, start, end)

        val rect = RectF(
            x,
            top.toFloat(),
            x + width + DRAW_PADDING * 2,
            y + BASELINE_PADDING
        )
        canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, paint)

        text?.let {
            paint.color = textColor
            canvas.drawText(
                it,
                start,
                end,
                x + DRAW_PADDING,
                y.toFloat(),
                paint
            )
        }
    }

    companion object {
        const val DRAW_PADDING = 24
        const val CORNER_RADIUS = 32F
        const val BASELINE_PADDING = 14F
    }
}
