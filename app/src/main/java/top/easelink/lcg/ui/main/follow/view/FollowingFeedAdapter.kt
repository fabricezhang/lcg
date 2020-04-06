package top.easelink.lcg.ui.main.follow.view

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.item_follow_content_view.view.*
import kotlinx.android.synthetic.main.item_load_more_view.view.*
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import top.easelink.framework.base.BaseViewHolder
import top.easelink.framework.utils.dpToPx
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.follow.model.FeedInfo
import top.easelink.lcg.ui.main.follow.viewmodel.FollowingFeedViewModel
import top.easelink.lcg.ui.main.model.OpenArticleEvent


class FollowingFeedAdapter(
    private val followingFeedViewModel: FollowingFeedViewModel,
    private val lifecycleOwner: LifecycleOwner
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
            VIEW_TYPE_NORMAL -> { ArticleViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_follow_content_view, parent, false))
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

    inner class ArticleViewHolder internal constructor(private val view: View) : BaseViewHolder(view) {
        @SuppressLint("SetTextI18n")
        override fun onBind(position: Int) {
            val feed = mFeeds[position]
            view.apply {
                open_article.setOnClickListener {
                    EventBus.getDefault().post(OpenArticleEvent(feed.articleUrl))
                }

                feed.avatar.let {
                    if (it.isNotEmpty()) {
                        avatar.visibility = View.VISIBLE
                        Glide
                            .with(this)
                            .load(it)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(RoundedCorners(4.dpToPx(context).toInt()))
                            .error(R.drawable.ic_noavatar_middle_gray)
                            .into(avatar)
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
                        preview.visibility = View.VISIBLE
                        Glide
                            .with(this)
                            .load(images[0])
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(RoundedCorners(4.dpToPx(context).toInt()))
                            .listener(object : RequestListener<Drawable>{
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any,
                                    target: Target<Drawable>,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    Timber.e(e)
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable,
                                    model: Any,
                                    target: Target<Drawable>,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    val newH = resource.intrinsicHeight.toFloat() / resource.intrinsicWidth.toFloat() * preview.width.toFloat()
                                    preview.layoutParams.height = newH.toInt()
                                    preview.setImageDrawable(resource)
                                    return true
                                }

                            })
                            .into(preview)
                    }?:run {
                        preview.visibility = View.INVISIBLE
                        preview.setImageDrawable(null)
                        preview.layoutParams.height = 0
                    }
                    content.setHtml(feed.content)
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