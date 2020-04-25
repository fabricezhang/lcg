package top.easelink.framework.customview.htmltextview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

class HtmlGlideImageGetter(private val context: Context, private val textView: TextView) :
    Html.ImageGetter {
    override fun getDrawable(url: String): Drawable {
        val drawable = BitmapDrawablePlaceholder()
        Glide.with(context)
            .asDrawable()
            .load(url)
            .fitCenter()
            .into(drawable)
        return drawable
    }

    private inner class BitmapDrawablePlaceholder internal constructor() :
        BitmapDrawable(context.resources, Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)),
        Target<Drawable> {
        private var drawable: Drawable? = null
        override fun draw(canvas: Canvas) {
            if (drawable != null) {
                drawable!!.draw(canvas)
            }
        }

        private fun setDrawable(drawable: Drawable) {
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

        override fun onLoadStarted(placeholderDrawable: Drawable?) {
            placeholderDrawable?.let { setDrawable(it) }
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            errorDrawable?.let { setDrawable(it) }
        }

        override fun onResourceReady(
            drawable: Drawable,
            transition: Transition<in Drawable>?
        ) {
            setDrawable(drawable)
        }

        override fun onLoadCleared(placeholderDrawable: Drawable?) {
            placeholderDrawable?.let { setDrawable(it) }
        }

        override fun getSize(cb: SizeReadyCallback) {
            textView.post { cb.onSizeReady(
                textView.measuredWidth,
                Target.SIZE_ORIGINAL
            )}
        }

        override fun removeCallback(cb: SizeReadyCallback) {}
        override fun setRequest(request: Request?) {}
        override fun getRequest(): Request? {
            return null
        }

        override fun onStart() {}
        override fun onStop() {}
        override fun onDestroy() {}
    }

}