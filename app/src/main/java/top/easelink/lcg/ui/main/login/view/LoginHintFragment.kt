package top.easelink.lcg.ui.main.login.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_login_hint.*
import top.easelink.framework.topbase.TopFragment
import top.easelink.lcg.R
import top.easelink.lcg.ui.webview.view.WebViewActivity

class LoginHintFragment : TopFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login_hint, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login_hint_instruction.setHtml(R.raw.login_instruction)
        login_hint_btn.setOnClickListener {
            WebViewActivity.openLoginPage(context)
        }
        message_animation.setOnClickListener {
            message_animation.playAnimation()
        }
    }

}
