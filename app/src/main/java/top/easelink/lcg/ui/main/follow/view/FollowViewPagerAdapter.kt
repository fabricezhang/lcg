package top.easelink.lcg.ui.main.follow.view

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.message.model.MessageTabModel
import top.easelink.lcg.utils.WebsiteConstant

class FollowViewPagerAdapter internal constructor(
    fm: FragmentManager,
    context: Context
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val tabModels: List<MessageTabModel> = listOf(
        MessageTabModel(context.getString(R.string.tab_title_following_feed), ""),
        MessageTabModel(
            context.getString(R.string.tab_title_following),
            WebsiteConstant.FOLLOWING_USERS_QUERY
        ),
        MessageTabModel(
            context.getString(R.string.tab_title_subscriber),
            WebsiteConstant.FOLLOWER_USERS_QUERY
        )
    )

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FollowingContentFragment()
            else -> {
                FollowDetailFragment(tabModels[position].url)
            }
        }
    }

    override fun getCount(): Int {
        return tabModels.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabModels[position].title
    }

}