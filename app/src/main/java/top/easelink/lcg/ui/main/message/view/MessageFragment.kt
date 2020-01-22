package top.easelink.lcg.ui.main.message.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.fragment_message.*
import top.easelink.framework.topbase.ControllableFragment
import top.easelink.framework.topbase.TopFragment
import top.easelink.lcg.R
import top.easelink.lcg.ui.info.UserData
import top.easelink.lcg.ui.main.login.view.LoginHintDialog
import top.easelink.lcg.ui.main.message.viewmodel.MessageViewPagerAdapter

class MessageFragment: TopFragment(), ControllableFragment{

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
        if (!UserData.loggedInState) {
            (mContext as? AppCompatActivity)?.let {
                LoginHintDialog().show(it.supportFragmentManager, null)
            }
        } else {
            message_view_pager.adapter = MessageViewPagerAdapter(childFragmentManager, mContext)
            message_tab.setupWithViewPager(message_view_pager)
        }
    }
}