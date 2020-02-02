package top.easelink.lcg.ui.main.forumnav2.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_navigation_view.view.*
import org.greenrobot.eventbus.EventBus
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.model.OpenForumEvent

abstract class BaseNavigationViewHolder(view: View) : RecyclerView.ViewHolder(view)

class ForumNavigationVH(inflater: LayoutInflater, parentView: ViewGroup): BaseNavigationViewHolder(
    inflater.inflate(R.layout.item_navigation_view, parentView, false)
) {
    fun onBind(item: ForumNavigationItem, payloads: List<Any>?) {
        val model = item.forumNavigationModel
        itemView.apply {
            forum_title.text = model.title
            forum_desc.text = model.description
            forum_icon.setBackgroundResource(model.drawableRes)
            setOnClickListener {
                EventBus.getDefault().post(OpenForumEvent(model.title, model.url, true))
            }
        }
    }
}