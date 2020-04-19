package top.easelink.lcg.ui.main.discover.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_forums_grid.view.*
import org.greenrobot.eventbus.EventBus
import top.easelink.framework.base.BaseViewHolder
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.discover.model.ForumNavigationModel
import top.easelink.lcg.ui.main.forumnav.view.ForumNavigationFragment
import top.easelink.lcg.ui.main.model.OpenForumEvent


class ForumNavigationAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private val mForumItems: MutableList<ForumNavigationModel> = mutableListOf()
    override fun getItemCount(): Int {
        return mForumItems.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == mForumItems.size) {
            VIEW_TYPE_LOAD_MORE
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> ForumViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_forums_grid, parent, false)
            )
            VIEW_TYPE_LOAD_MORE -> LoadMoreViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_forums_grid, parent, false
                )
            )
            else -> throw IllegalStateException()
        }
    }

    fun addItems(forumModels: List<ForumNavigationModel>) {
        mForumItems.addAll(forumModels)
        notifyDataSetChanged()
    }
    
    inner class ForumViewHolder internal constructor(view: View) : BaseViewHolder(view) {

        private fun onItemClick(title: String, url: String) {
            EventBus.getDefault().post(OpenForumEvent(title, url, true))
        }

        override fun onBind(position: Int) {
            val forumModel = mForumItems[position]
            itemView.run {
                setOnClickListener {
                    onItemClick(forumModel.title, forumModel.url)
                }
                grid_text.text = forumModel.title
                grid_image.setImageResource(forumModel.drawableRes)
            }
        }

    }

    inner class LoadMoreViewHolder internal constructor(val view: View) : BaseViewHolder(view) {
        override fun onBind(position: Int) {
            itemView.run {
                setOnClickListener {
                    EventBus.getDefault().post(ForumNavigationFragment::class.java)
                }
                grid_text.text =  context.getText(R.string.more_forums)
                grid_image.setImageResource(R.drawable.ic_more)
            }
        }
    }

    companion object {
        const val VIEW_TYPE_NORMAL = 1
        const val VIEW_TYPE_LOAD_MORE = 2
    }
}