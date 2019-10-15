package top.easelink.lcg.ui.main.login.view

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProviders
import top.easelink.framework.base.SafeShowDialogFragment
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.login.viewmodel.LoginViewModel

class LoginDialog : SafeShowDialogFragment() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)[LoginViewModel::class.java]
        setStyle(STYLE_NORMAL, R.style.AppTheme_Dialog_FullScreen_BottomInOut)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_login, container, false)
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
        viewModel.fetchLoginPage()
    }
}
