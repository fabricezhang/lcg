package top.easelink.lcg.ui.main.me.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import top.easelink.framework.base.BaseViewHolder
import top.easelink.framework.customview.htmltextview.HtmlTextView
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.model.SystemNotification

class NotificationsAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    private val systemNotifications: MutableList<SystemNotification> = mutableListOf()

    override fun getItemCount(): Int {
        return if (systemNotifications.isEmpty()) {
            1
        } else {
            systemNotifications.size + 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (systemNotifications.isEmpty()) {
            VIEW_TYPE_EMPTY
        } else {
            if (position == systemNotifications.size) {
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

    fun addItems(notifications: List<SystemNotification>) {
        systemNotifications.addAll(notifications)
        notifyDataSetChanged()
    }

    fun clearItems() {
        systemNotifications.clear()
    }

    inner class ArticleViewHolder internal constructor(private val view: View) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {
            val notification = systemNotifications[position]
            view.apply {
                findViewById<HtmlTextView>(R.id.notification_title).setHtml(notification.title)
                findViewById<TextView>(R.id.date_time).text = notification.dateTime
            }
        }

    }

    inner class EmptyViewHolder(view: View) : BaseViewHolder(view){
        override fun onBind(position: Int) { }
    }

    inner class LoadMoreViewHolder internal constructor(view: View) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {

        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_NORMAL = 1
        private const val VIEW_TYPE_LOAD_MORE = 2
    }

}