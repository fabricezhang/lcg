package top.easelink.lcg.ui.main.logout.view

import android.os.Bundle
import android.view.*
import kotlinx.android.synthetic.main.dialog_logout_hint.*
import top.easelink.framework.topbase.TopDialog
import top.easelink.framework.utils.dpToPx
import top.easelink.lcg.R
import top.easelink.lcg.account.UserDataRepo
import top.easelink.lcg.appinit.LCGApp

class LogoutHintDialog(
    private val positive: (() -> Unit)? = null,
    private val negative: (() -> Unit)? = null
) : TopDialog() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setWindowAnimations(R.style.FadeInOutAnim)
        return inflater.inflate(R.layout.dialog_logout_hint, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logout_message.text = String.format(getString(R.string.logout_confirm_message, UserDataRepo.username))
        logout_confirm_btn.setOnClickListener {
            positive?.invoke()
            dismissDialog()
        }
        cancel_btn.setOnClickListener {
            negative?.invoke()
            dismissDialog()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val window = dialog?.window
        if (window != null) {
            val windowParam = window.attributes
            windowParam.width = 300.dpToPx(LCGApp.context).toInt()
            windowParam.height = WindowManager.LayoutParams.WRAP_CONTENT
            windowParam.gravity = Gravity.CENTER
            window.attributes = windowParam
        }
    }

}
