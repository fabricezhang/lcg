package top.easelink.framework.customview.htmltextview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.TextView
import coil.Coil
import coil.request.ImageRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.easelink.framework.threadpool.IOPool

class HtmlCoilImageGetter(
    private val context: Context,
    private val textView: TextView
) : Html.ImageGetter {

    override fun getDrawable(url: String): Drawable {
        val holder = BitmapDrawablePlaceholder()
        GlobalScope.launch(IOPool) {
            Coil.imageLoader(context).enqueue(
                ImageRequest.Builder(context)
                    .data(url)
                    .target {
                        holder.setDrawable(it)
                    }
                    .build()
            )
        }
        return holder
    }

    private inner class BitmapDrawablePlaceholder : BitmapDrawable(context.resources, Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)) {
        private var drawable: Drawable? = null
        override fun draw(canvas: Canvas) {
            drawable?.draw(canvas)
        }

        fun setDrawable(drawable: Drawable) {
            this.drawable = drawable
            var drawableWidth = drawable.intrinsicWidth
            var drawableHeight = drawable.intrinsicHeight
            if (drawableWidth > 300) {
                val maxWidth = textView.measuredWidth
                drawableHeight = maxWidth * drawableHeight / drawableWidth
                drawableWidth = maxWidth
            }
            drawable.setBounds(0, 0, drawableWidth, drawableHeight)
            setBounds(0, 0, drawableWidth, drawableHeight)
            textView.text = textView.text
        }
    }

}