package com.edu.lite.utils
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class CircularMaskOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val overlayPaint = Paint().apply {
        color = Color.parseColor("#FFFFFFFF") // semi-transparent black
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val clearPaint = Paint().apply {
        color = Color.TRANSPARENT
        xfermode  = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val saveCount = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

        // Fill the whole view with semi-transparent color
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)

        // Calculate circle hole size and position (center of square)
        val size = width.coerceAtMost(height)
        val radius = size / 4f
        val cx = width / 2f
        val cy = height / 2f

        // Cut out the circular area
        canvas.drawCircle(cx, cy, radius, clearPaint)

        canvas.restoreToCount(saveCount)
    }
}
