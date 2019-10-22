package top.easelink.framework.utils

import android.content.Context
import android.util.DisplayMetrics


fun px2dp(px: Float, context: Context): Float {
    val resources = context.resources
    val metrics = resources.displayMetrics
    return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun dp2px(context: Context, dp: Float): Float {
    return dp * context.resources.displayMetrics.density
}