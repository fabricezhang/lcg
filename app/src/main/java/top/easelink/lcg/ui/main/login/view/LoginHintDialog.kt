package top.easelink.lcg.ui.main.login.view

import android.os.Bundle
import android.view.*
import kotlinx.android.synthetic.main.dialog_login_hint.*
import top.easelink.framework.topbase.TopDialog
import top.easelink.lcg.R
import top.easelink.lcg.ui.webview.view.WebViewActivity

class LoginHintDialog : TopDialog() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_Dialog_FullScreen_BottomInOut)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setWindowAnimations(R.style.BottomInOutAnim)
        return inflater.inflate(R.layout.dialog_login_hint, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login_hint_instruction.setHtml(R.raw.login_instruction)
        login_hint_btn.setOnClickListener {
            WebViewActivity.openLoginPage(mContext)
            dismissDialog()
        }
        login_cancel_btn.setOnClickListener {
            dismissDialog()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val window = dialog?.window
        if (window != null) {
            val windowParam = window.attributes
            windowParam.width = WindowManager.LayoutParams.MATCH_PARENT
            windowParam.height = WindowManager.LayoutParams.WRAP_CONTENT
            windowParam.gravity = Gravity.BOTTOM
            window.attributes = windowParam
        }
    }

}
