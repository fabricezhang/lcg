package top.easelink.lcg.ui.main.follow.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.load
import coil.request.ImageRequest
import coil.size.OriginalSize
import coil.size.SizeResolver
import coil.transform.RoundedCornersTransformation
import kotlinx.android.synthetic.main.item_follow_content_view.view.*
import kotlinx.android.synthetic.main.item_load_more_view.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import top.easelink.framework.base.BaseViewHolder
import top.easelink.framework.threadpool.IOPool
import top.easelink.framework.utils.dpToPx
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.follow.model.FeedInfo
import top.easelink.lcg.ui.main.follow.viewmodel.FollowingFeedViewModel
import top.easelink.lcg.ui.main.model.OpenArticleEvent
import top.easelink.lcg.ui.main.model.OpenLargeImageViewEvent


class FollowingFeedAdapter(
    private val followingFeedViewModel: FollowingFeedViewModel
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val mFeeds: MutableList<FeedInfo> = mutableListOf()

    override fun getItemCount(): Int {
        return if (mFeeds.isEmpty()) {
            1
        } else {
            mFeeds.size + 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mFeeds.isEmpty()) {
            VIEW_TYPE_EMPTY
        } else {
            if (position == mFeeds.size) {
                VIEW_TYPE_LOAD_MORE
            } else VIEW_TYPE_NORMAL
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> {
                ArticleViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_follow_content_view, parent, false)
                )
            }
            VIEW_TYPE_LOAD_MORE -> LoadMoreViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_load_more_view, parent, false)
            )
            else -> EmptyViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_empty_view, parent, false)
            )
        }
    }

    fun addItems(follows: List<FeedInfo>) {
        mFeeds.addAll(follows)
        notifyDataSetChanged()
    }

    fun appendItems(follows: List<FeedInfo>) {
        val count = itemCount
        mFeeds.addAll(follows)
        notifyItemRangeInserted(count - 1, follows.size)
    }

    fun clearItems() {
        mFeeds.clear()
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        if (holder is LoadMoreViewHolder) {
            holder.removeObserver()
        }
        super.onViewRecycled(holder)
    }

    inner class ArticleViewHolder internal constructor(
        private val view: View
    ) : BaseViewHolder(view) {
        @SuppressLint("SetTextI18n")
        override fun onBind(position: Int) {
            val feed = mFeeds[position]
            view.apply {
                val round = 4.dpToPx(context)
                open_article.setOnClickListener {
                    EventBus.getDefault().post(OpenArticleEvent(feed.articleUrl))
                }

                feed.avatar.let {
                    if (it.isNotEmpty()) {
                        avatar.visibility = View.VISIBLE
                        avatar.load(it) {
                            transformations(RoundedCornersTransformation(round))
                            error(R.drawable.ic_noavatar_middle_gray)
                            crossfade(true)
                        }
                    } else {
                        avatar.visibility = View.GONE
                        avatar.setImageDrawable(null)
                    }
                }
                username.text = feed.username
                date_time.text = feed.dateTime
                title.text = feed.title
                forum.text = "#${feed.forum}"
                if (feed.quote.isNotBlank()) {
                    content.setHtml(feed.quote)
                    preview.visibility = View.INVISIBLE
                    preview.setImageDrawable(null)
                    preview.layoutParams.also {
                        it.height = 0
                    }
                } else {
                    feed.images?.takeIf {
                        it.isNotEmpty()
                    }?.let { images ->
                        preview.setOnClickListener {
                            EventBus.getDefault().post(OpenLargeImageViewEvent(images[0]))
                        }
                        preview.visibility = View.VISIBLE
                        GlobalScope.launch(IOPool) {
                            ImageRequest.Builder(context)
                                .data(images[0])
                                .size(SizeResolver(OriginalSize))
                                .transformations(RoundedCornersTransformation(round))
                                .target {
                                    val newH =
                                        it.intrinsicHeight.toFloat() / it.intrinsicWidth.toFloat() * preview.width.toFloat()
                                    preview.apply {
                                        layoutParams.height = newH.toInt()
                                        setImageDrawable(it)
                                    }
                                }.build()
                                .let {
                                    Coil.imageLoader(context).enqueue(it)
                                }
                        }
                    } ?: run {
                        preview.visibility = View.INVISIBLE
                        preview.setImageDrawable(null)
                        preview.layoutParams.height = 0
                    }
                    content.setHtml(feed.content)
                }
            }
        }

    }

    class EmptyViewHolder(view: View) : BaseViewHolder(view) {
        override fun onBind(position: Int) {}
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
            followingFeedViewModel.fetchMore {
                if (!it) {
                    view.loading.apply {
                        cancelAnimation()
                        visibility = View.GONE
                    }
                    removeObserver()
                }
            }
        }

        fun removeObserver() {
            followingFeedViewModel.isLoadingForLoadMore.removeObserver(observer)
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_NORMAL = 1
        private const val VIEW_TYPE_LOAD_MORE = 2
    }

}