package top.easelink.lcg.ui.main.largeimg.view

import android.os.Bundle
import android.view.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import kotlinx.android.synthetic.main.dialog_large_image.*
import top.easelink.framework.topbase.TopDialog
import top.easelink.lcg.R

class LargeImageDialog(private val imageUrl: String) : TopDialog() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog?.window?.setWindowAnimations(R.style.FadeInOutAnim)
        return inflater.inflate(R.layout.dialog_large_image, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide
            .with(view)
            .load(imageUrl)
            .override(SIZE_ORIGINAL)
            .into(photo)
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
