package top.easelink.lcg.ui.main.follow.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_follow.*
import top.easelink.framework.topbase.ControllableFragment
import top.easelink.framework.topbase.TopFragment
import top.easelink.lcg.R
import top.easelink.lcg.mta.EVENT_OPEN_FOLLOW_PAGE
import top.easelink.lcg.mta.sendEvent

class FollowFragment: TopFragment(), ControllableFragment{

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_follow, container, false)
    }

    override fun isControllable(): Boolean {
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        follow_view_pager.adapter =
            FollowViewPagerAdapter(
                childFragmentManager,
                mContext
            )
        follow_tab.setupWithViewPager(follow_view_pager)
        sendEvent(EVENT_OPEN_FOLLOW_PAGE)
    }
}