package top.easelink.lcg.ui.main.logout.view

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import top.easelink.framework.base.SafeShowDialogFragment
import top.easelink.framework.utils.dpToPx
import top.easelink.lcg.LCGApp
import top.easelink.lcg.R
import top.easelink.lcg.spipedata.UserData

class LogoutHintDialog(
    private val positive: ()-> Unit,
    private val negative: () -> Unit
) : SafeShowDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog?.window?.setWindowAnimations(R.style.FadeInOutAnim)
        return inflater.inflate(R.layout.dialog_logout_hint, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.logout_message).text =
            String.format(
                getString(
                    R.string.logout_confirm_message,
                    UserData.username)
            )
        view.findViewById<Button>(R.id.logout_confirm_btn).setOnClickListener {
            positive.invoke()
            dismissDialog()
        }
        view.findViewById<Button>(R.id.cancel_btn).setOnClickListener {
            negative.invoke()
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
