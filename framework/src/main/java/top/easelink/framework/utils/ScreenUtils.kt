package top.easelink.framework.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import timber.log.Timber

fun Int.dpToPx(context: Context) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    context.resources.displayMetrics
)

fun Float.dpToPx(context: Context) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)

fun px2dp(px: Float, context: Context): Float {
    val resources = context.resources
    val metrics = resources.displayMetrics
    return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun dp2px(context: Context, dp: Float): Float {
    return dp * context.resources.displayMetrics.density
}

fun convertViewToBitmap(view: View): Bitmap? {
    try {
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.layout(view.left, view.top, view.right, view.bottom)
        view.draw(canvas)
        return bitmap
    } catch (e: Exception) {
        Timber.e(e)
    }
    return null
}