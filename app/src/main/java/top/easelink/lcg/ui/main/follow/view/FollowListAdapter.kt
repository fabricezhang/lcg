package top.easelink.lcg.ui.main.follow.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import kotlinx.android.synthetic.main.item_follow_view.view.*
import kotlinx.android.synthetic.main.item_load_more_view.view.*
import top.easelink.framework.base.BaseViewHolder
import top.easelink.framework.utils.dpToPx
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.follow.model.FollowInfo
import top.easelink.lcg.ui.main.follow.viewmodel.FollowListViewModel


class FollowListAdapter(
    private val followListViewModel: FollowListViewModel,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val mFollowing: MutableList<FollowInfo> = mutableListOf()
    var nextPageUrl: String? = null

    override fun getItemCount(): Int {
        return if (mFollowing.isEmpty()) {
            1
        } else {
            mFollowing.size + 1
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

    override fun onViewRecycled(holder: BaseViewHolder) {
        if (holder is LoadMoreViewHolder) {
            holder.removeObserver()
        }
        super.onViewRecycled(holder)
    }

    inner class ArticleViewHolder internal constructor(private val view: View) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {
            val follow = mFollowing[position]
            view.apply {
                follow.avatar.let {
                    avatar.load(it) {
                        transformations(RoundedCornersTransformation(2.dpToPx(context)))
                            .error(R.drawable.ic_noavatar_middle_gray)
                    }
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

    inner class LoadMoreViewHolder internal constructor(val view: View) : BaseViewHolder(view) {

        val observer: Observer<Boolean> = Observer {
            view.loading.apply {
                if (it) {
                    visibility = View.VISIBLE
                    playAnimation()
                } else {
                    cancelAnimation()
                    visibility = View.GONE
                }
            }
        }

        override fun onBind(position: Int) {
            nextPageUrl?.let {
                followListViewModel.isLoadingForLoadMore.observe(lifecycleOwner, observer)
                followListViewModel.fetchData(it, true)
            }?: run {
                view.loading.apply {
                    cancelAnimation()
                    visibility = View.GONE
                }
            }
        }

        fun removeObserver() {
            followListViewModel.isLoadingForLoadMore.removeObserver(observer)
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_NORMAL = 1
        private const val VIEW_TYPE_LOAD_MORE = 2
    }

}