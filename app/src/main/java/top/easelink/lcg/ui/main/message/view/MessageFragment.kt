package top.easelink.lcg.ui.main.message.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_message.*
import top.easelink.framework.topbase.ControllableFragment
import top.easelink.framework.topbase.TopFragment
import top.easelink.lcg.R
import top.easelink.lcg.account.AccountManager
import top.easelink.lcg.ui.main.login.view.LoginHintDialog

class MessageFragment : TopFragment(), ControllableFragment {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun isControllable(): Boolean {
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AccountManager.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn) {
                message_view_pager.adapter =
                    MessageViewPagerAdapter(
                        childFragmentManager,
                        mContext
                    )
                message_tab.setupWithViewPager(message_view_pager)
            } else {
                (mContext as? AppCompatActivity)?.let {
                    LoginHintDialog().show(it.supportFragmentManager, null)
                }
            }
        }
    }
}