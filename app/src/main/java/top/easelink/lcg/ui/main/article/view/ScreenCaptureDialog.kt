package top.easelink.lcg.ui.main.article.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.dialog_screen_capture.*
import timber.log.Timber
import top.easelink.framework.base.SafeShowDialogFragment
import top.easelink.lcg.R

class ScreenCaptureDialog : SafeShowDialogFragment() {

    companion object {
        val TAG: String = ScreenCaptureDialog::class.java.simpleName
        private const val IMAGE_PATH = "image_path"
        @JvmStatic
        fun newInstance(imagePath: String): ScreenCaptureDialog {
            return ScreenCaptureDialog().apply {
                arguments = Bundle().apply {
                    putString(IMAGE_PATH, imagePath)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_Dialog_FullScreen_BottomInOut)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_screen_capture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val path = arguments?.getString(IMAGE_PATH)
            Glide
                .with(view)
                .load(path)
                .skipMemoryCache(true)
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        img_screen_capture.post {
                            img_screen_capture.setImageDrawable(resource)
                        }
                        return true
                    }

                })
                .submit()

        } catch (e: Exception) {
            Timber.e(e)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            gravity = Gravity.CENTER
        }
    }

}
