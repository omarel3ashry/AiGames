package com.example.android.aigames.sudoku.tagSphere

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextPaint
import com.example.android.aigames.sudoku.tagSphere.item.TagItem
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Created by Omar Elashry on 2020-11-21.
 */

class VectorDrawableTagItem(private val drawable: Drawable,private val drawableTagId: String) : TagItem() {

    init {
        drawable.bounds = Rect(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    }

    override fun setTagId(tagId: String) {
        super.setTagId(drawableTagId)
    }

    override fun getTagId(): String {
        return drawableTagId
    }

    override fun drawSelf(
            x: Float,
            y: Float,
            canvas: Canvas,
            paint: TextPaint,
            easingFunction: ((t: Float) -> Float)?
    ) {
        canvas.save()
        canvas.translate(x - drawable.intrinsicWidth / 2f, y - drawable.intrinsicHeight / 2f)
        val alpha = easingFunction?.let { calc ->
            val ease = calc(getEasingValue())
            if (!ease.isNaN()) max(0, min(255, (255 * ease).roundToInt())) else 0
        } ?: 255
        drawable.alpha = alpha
        drawable.draw(canvas)
        canvas.restore()
    }
}