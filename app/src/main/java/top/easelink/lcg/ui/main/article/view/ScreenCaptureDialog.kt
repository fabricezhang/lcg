package top.easelink.lcg.ui.main.article.view

import android.os.Bundle
import android.view.*
import coil.Coil
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.OriginalSize
import coil.size.SizeResolver
import kotlinx.android.synthetic.main.dialog_screen_capture.*
import kotlinx.android.synthetic.main.dialog_screen_capture.view.*
import timber.log.Timber
import top.easelink.framework.topbase.TopDialog
import top.easelink.lcg.R
import top.easelink.lcg.utils.startWeChat
import top.easelink.lcg.utils.syncSystemGallery

class ScreenCaptureDialog : TopDialog() {

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_screen_capture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            arguments?.getString(IMAGE_PATH)?.also { path ->
                ImageRequest.Builder(view.context)
                    .data(path)
                    .size(SizeResolver(OriginalSize))
                    .diskCachePolicy(CachePolicy.DISABLED)
                    .target {
                        img_screen_capture.post {
                            img_screen_capture.setImageDrawable(it)
                        }
                    }.build().let {
                        Coil.imageLoader(view.context).enqueue(it)
                    }
                view.share.setOnClickListener { share ->
                    syncSystemGallery(share.context, path, path.let {
                        val p = it.lastIndexOf("/")
                        if (p >= it.length) {
                            throw IllegalArgumentException("path is not a file!")
                        } else {
                            it.substring(p + 1)
                        }
                    })
                    startWeChat(share.context)
                }
            }
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
