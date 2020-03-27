package top.easelink.lcg.ui.main.message.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_load_more_view.view.*
import kotlinx.android.synthetic.main.item_notification_view.view.*
import top.easelink.framework.base.BaseViewHolder
import top.easelink.framework.customview.htmltextview.HtmlGlideImageGetter
import top.easelink.framework.utils.dpToPx
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.message.viewmodel.NotificationViewModel
import top.easelink.lcg.ui.main.model.BaseNotification


class NotificationsAdapter(
    val notificationViewModel: NotificationViewModel
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val mNotifications: MutableList<BaseNotification> = mutableListOf()

    override fun getItemCount(): Int {
        return if (mNotifications.isEmpty()) {
            1
        } else {
            mNotifications.size + 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mNotifications.isEmpty()) {
            VIEW_TYPE_EMPTY
        } else {
            if (position == mNotifications.size) {
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
                    .inflate(R.layout.item_notification_view, parent, false))
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

    fun addItems(notifications: List<BaseNotification>) {
        mNotifications.addAll(notifications)
        notifyDataSetChanged()
    }

    fun appendItems(notifications: List<BaseNotification>) {
        val count = itemCount
        mNotifications.addAll(notifications)
        notifyItemRangeInserted(count - 1, notifications.size)
    }

    fun clearItems() {
        mNotifications.clear()
    }

    inner class ArticleViewHolder internal constructor(private val view: View) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {
            val notification = mNotifications[position]
            view.apply {
                line.visibility = if(position == 0) View.GONE else View.VISIBLE
                notification_title.apply {
                    setHtml(notification.content, HtmlGlideImageGetter(
                        context,
                        this
                    ))
                    linksClickable = false
                }
                date_time.text = notification.dateTime
                Glide.with(notification_avatar)
                    .load(notification.avatar)
                    .apply(RequestOptions.bitmapTransform(
                        RoundedCorners(8.dpToPx(view.context).toInt())
                    ))
                    .placeholder(R.drawable.ic_noavatar_middle_gray)
                    .into(notification_avatar)
            }
        }

    }

    inner class EmptyViewHolder(view: View) : BaseViewHolder(view){
        override fun onBind(position: Int) { }
    }

    inner class LoadMoreViewHolder internal constructor(val view: View) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {
            view.loading.visibility = View.VISIBLE
            notificationViewModel.fetchMoreNotifications {
                view.post {
                    view.loading.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_NORMAL = 1
        private const val VIEW_TYPE_LOAD_MORE = 2
    }

}