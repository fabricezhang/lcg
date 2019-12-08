package top.easelink.lcg.ui.main.article.view

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import timber.log.Timber
import top.easelink.framework.base.SafeShowDialogFragment
import top.easelink.lcg.R
import top.easelink.lcg.utils.ActivityUtils

class ScreenCaptureDialog : SafeShowDialogFragment() {

    companion object {
        val TAG = ActivityUtils.TAG_PREFIX + ScreenCaptureDialog::class.java.simpleName
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
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog_FullScreen_BottomInOut)
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
                .into(view.findViewById(R.id.img_screen_capture))
        } catch (e: Exception) {
            Timber.e(e)
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
