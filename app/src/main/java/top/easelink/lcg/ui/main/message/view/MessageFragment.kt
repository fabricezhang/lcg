package top.easelink.lcg.ui.main.message.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_message.*
import top.easelink.framework.topbase.ControllableFragment
import top.easelink.framework.topbase.TopFragment
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.message.viewmodel.MessageViewModel
import top.easelink.lcg.ui.main.message.viewmodel.MessageViewPagerAdapter

class MessageFragment: TopFragment(), ControllableFragment{

    lateinit var pmVM: MessageViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pmVM = ViewModelProviders.of(this).get(MessageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    private fun setUp(){
        message_view_pager.adapter = MessageViewPagerAdapter(childFragmentManager, mContext)
        message_tab.setupWithViewPager(message_view_pager)
    }
}