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

fun convertViewToBitmap(view: View?, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? {
    view?.apply {
        try {
            val bitmap = Bitmap.createBitmap(
                measuredWidth,
                measuredHeight,
                config
            )
            val canvas = Canvas(bitmap)
            layout(left, top, right, bottom)
            draw(canvas)
            return bitmap
        } catch (re: RuntimeException) {
            Timber.e(re)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
    return null
}