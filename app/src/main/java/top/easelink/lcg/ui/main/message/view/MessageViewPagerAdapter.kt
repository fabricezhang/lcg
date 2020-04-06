package top.easelink.lcg.ui.main.message.view

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.message.model.MessageTabModel

class MessageViewPagerAdapter internal constructor(
    fm: FragmentManager,
    context: Context
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val tabModels: List<MessageTabModel> = listOf(
        MessageTabModel(context.getString(R.string.tab_title_notification), ""),
        MessageTabModel(context.getString(R.string.tab_title_private_message), "")
    )

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> NotificationFragment()
            1 -> ConversationListFragment()
            else -> throw IllegalStateException("can't reach here")
        }
    }

    override fun getCount(): Int {
        return tabModels.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabModels[position].title
    }

}