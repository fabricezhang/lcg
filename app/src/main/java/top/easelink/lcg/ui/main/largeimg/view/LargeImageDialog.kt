package top.easelink.lcg.ui.main.largeimg.view

import android.os.Bundle
import android.view.*
import coil.load
import coil.size.OriginalSize
import coil.size.SizeResolver
import kotlinx.android.synthetic.main.dialog_large_image.*
import top.easelink.framework.topbase.TopDialog
import top.easelink.lcg.R

class LargeImageDialog : TopDialog() {

    companion object {
        private const val IMAGE_URL = "image_url"

        fun newInstance(imageUrl: String): LargeImageDialog {
            return LargeImageDialog().also {
                it.arguments = Bundle().apply {
                    putString(IMAGE_URL, imageUrl)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setWindowAnimations(R.style.FadeInOutAnim)
        return inflater.inflate(R.layout.dialog_large_image, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(IMAGE_URL)?.let {
            photo.load(it) {
                size(SizeResolver(OriginalSize))
            }
        }
        exit.setOnClickListener {
            dismissDialog()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val window = dialog?.window
        if (window != null) {
            val windowParam = window.attributes
            windowParam.width = WindowManager.LayoutParams.MATCH_PARENT
            windowParam.height = WindowManager.LayoutParams.MATCH_PARENT
            windowParam.gravity = Gravity.CENTER
            window.attributes = windowParam
        }
    }

}
