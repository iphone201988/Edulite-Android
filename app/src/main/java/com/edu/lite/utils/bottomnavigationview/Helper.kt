package com.edu.lite.utils.bottomnavigationview

import android.content.Context
import android.util.DisplayMetrics
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat


fun Int.toPx(context: Context) = (this * context.resources.displayMetrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT

fun Context.getColorRes(@ColorRes colorId: Int) = ContextCompat.getColor(this, colorId)

private fun dpToPx(context: Context, dp: Int): Int {
    return (dp * context.resources.displayMetrics.density).toInt()
}

