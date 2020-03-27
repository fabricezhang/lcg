package top.easelink.lcg.ui.main.follow.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_follow_view.view.*
import kotlinx.android.synthetic.main.item_load_more_view.view.*
import top.easelink.framework.base.BaseViewHolder
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.follow.model.FollowInfo


class FollowListAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    private val mFollowing: MutableList<FollowInfo> = mutableListOf()

    override fun getItemCount(): Int {
        return if (mFollowing.isEmpty()) {
            1
        } else {
            mFollowing.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mFollowing.isEmpty()) {
            VIEW_TYPE_EMPTY
        } else {
            if (position == mFollowing.size) {
                VIEW_TYPE_LOAD_MORE
            } else VIEW_TYPE_NORMAL
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> { ArticleViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_follow_view, parent, false))
            }
            VIEW_TYPE_LOAD_MORE -> LoadMoreViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_load_more_view, parent, false)
            )
            else -> EmptyViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_empty_view, parent, false))
        }
    }

    fun addItems(follows: List<FollowInfo>) {
        mFollowing.addAll(follows)
        notifyDataSetChanged()
    }

    fun appendItems(follows: List<FollowInfo>) {
        val count = itemCount
        mFollowing.addAll(follows)
        notifyItemRangeInserted(count - 1, follows.size)
    }

    fun clearItems() {
        mFollowing.clear()
    }

    inner class ArticleViewHolder internal constructor(private val view: View) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {
            val follow = mFollowing[position]
            view.apply {
                follow.avatar.let {
                    Glide
                        .with(this)
                        .load(it)
                        .error(R.drawable.ic_noavatar_middle_gray)
                        .into(avatar)
                }
                last_action.text = follow.lastAction
                username.text = follow.username
                follower_num.text = context.getString(R.string.follower_num_template, follow.followerNum)
                following_num.text = context.getString(R.string.following_num_template, follow.followingNum)
                follow_list_container.setOnClickListener {
                    //open article
                }
            }
        }

    }

    inner class EmptyViewHolder(view: View) : BaseViewHolder(view){
        override fun onBind(position: Int) { }
    }

    inner class LoadMoreViewHolder internal constructor(val view: View) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {
            view.loading.visibility = View.GONE
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_NORMAL = 1
        private const val VIEW_TYPE_LOAD_MORE = 2
    }

}