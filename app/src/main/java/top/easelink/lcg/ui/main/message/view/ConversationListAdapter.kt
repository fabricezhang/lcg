package top.easelink.lcg.ui.main.message.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.android.synthetic.main.item_conversation_view.view.*
import kotlinx.android.synthetic.main.item_load_more_view.view.*
import top.easelink.framework.base.BaseViewHolder
import top.easelink.framework.utils.dpToPx
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.message.viewmodel.ConversationListViewModel
import top.easelink.lcg.ui.main.model.Conversation
import top.easelink.lcg.ui.webview.view.WebViewActivity
import top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL


class ConversationListAdapter(
    val mConversationVM: ConversationListViewModel
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val mConversations: MutableList<Conversation> = mutableListOf()

    override fun getItemCount(): Int {
        return if (mConversations.isEmpty()) {
            1
        } else {
            // todo add load more in the future
            mConversations.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mConversations.isEmpty()) {
            VIEW_TYPE_EMPTY
        } else {
            if (position == mConversations.size) {
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
                    .inflate(R.layout.item_conversation_view, parent, false))
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

    fun addItems(conversations: List<Conversation>) {
        mConversations.addAll(conversations)
        notifyDataSetChanged()
    }

    fun appendItems(conversations: List<Conversation>) {
        val count = itemCount
        mConversations.addAll(conversations)
        notifyItemRangeInserted(count - 1, conversations.size)
    }

    fun clearItems() {
        mConversations.clear()
    }

    inner class ArticleViewHolder internal constructor(private val view: View) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {
            val conversation = mConversations[position]
            view.apply {
                date_time.text = conversation.lastMessageDateTime
                conversation.avatar?.let {
                    Glide
                        .with(this)
                        .load(it)
                        .transform(RoundedCorners(6.dpToPx(context).toInt()))
                        .error(R.drawable.ic_noavatar_middle_gray)
                        .into(conversation_user_avatar)
                }
                last_message.text = conversation.lastMessage
                username.text = conversation.username
                conversation_list_container.setOnClickListener {
                    WebViewActivity.startWebViewWith(SERVER_BASE_URL + conversation.replyUrl, context)
//                    context.startActivity(Intent(context, ConversationDetailActivity::class.java))
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
            //TODO add fetch more in the future
            view.loading.visibility = View.GONE
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_NORMAL = 1
        private const val VIEW_TYPE_LOAD_MORE = 2
    }

}