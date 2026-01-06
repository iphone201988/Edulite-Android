package com.edu.lite.ui.dash_board.road_map



import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View



class VerticalLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr)
{

    private val backgroundGray = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#D9D9D9")
        strokeWidth = 200f
    }

    private val innerDark = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#212121")
        strokeWidth = 174f
    }

    private val whiteSolid = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = 6f
    }

    private val whiteDashed = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = 3f
        pathEffect = DashPathEffect(floatArrayOf(15.03f, 15.03f), 0f)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val top = 0f
        val bottom = height.toFloat()

        // Gray background line
        canvas.drawLine(centerX, top, centerX, bottom, backgroundGray)

        // Dark inner line
        canvas.drawLine(centerX, top, centerX, bottom, innerDark)

        // Two white side lines (left and right)
        canvas.drawLine(centerX - 50.5f, top, centerX - 50.5f, bottom, whiteSolid)
        canvas.drawLine(centerX + 50.5f, top, centerX + 50.5f, bottom, whiteSolid)

        // Center dashed white line
        canvas.drawLine(centerX, top, centerX, bottom, whiteDashed)
    }
}
