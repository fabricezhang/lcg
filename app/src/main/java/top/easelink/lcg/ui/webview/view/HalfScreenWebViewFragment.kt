package top.easelink.lcg.ui.webview.view

import android.os.Bundle
import android.view.*
import android.webkit.WebSettings
import kotlinx.android.synthetic.main.activity_web_view.*
import top.easelink.framework.topbase.TopDialog
import top.easelink.lcg.R
import top.easelink.lcg.utils.getScreenHeight

class HalfScreenWebViewFragment: TopDialog() {
    companion object {
        private const val HTML = "html"
        fun newInstance(html: String): HalfScreenWebViewFragment {
            val args = Bundle().apply {
                putString(HTML, html)
            }
            val fragment = HalfScreenWebViewFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setWindowAnimations(R.style.BottomInOutAnim)
        return inflater.inflate(R.layout.dialog_half_screen_webview, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(HTML)?.let {
            updateWebViewSettingsLocal()
            web_view.loadDataWithBaseURL("", it, "text/html", "UTF-8", "")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val window = dialog?.window
        if (window != null) {
            val windowParam = window.attributes
            dialog?.setCanceledOnTouchOutside(true)
            context?.let { ctx ->
                windowParam.width = WindowManager.LayoutParams.MATCH_PARENT
                windowParam.height = getScreenHeight(ctx) / 2
                windowParam.gravity = Gravity.BOTTOM
                window.attributes = windowParam
            }
        }
    }

    private fun updateWebViewSettingsLocal() {
        web_view.setScrollEnable(true)
        web_view.settings.apply {
            // Zoom Setting
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            blockNetworkImage = false
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            defaultTextEncodingName = "UTF-8"
            builtInZoomControls = true
            setAppCacheEnabled(true)
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        
    }
}