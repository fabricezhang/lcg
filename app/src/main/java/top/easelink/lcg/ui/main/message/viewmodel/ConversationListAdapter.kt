package top.easelink.lcg.ui.main.message.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import top.easelink.framework.base.BaseViewHolder
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.model.Conversation


class ConversationListAdapter(
    val mConversationVM: ConversationListViewModel
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val mConversations: MutableList<Conversation> = mutableListOf()

    override fun getItemCount(): Int {
        return if (mConversations.isEmpty()) {
            1
        } else {
            mConversations.size + 1
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
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_NORMAL = 1
        private const val VIEW_TYPE_LOAD_MORE = 2
    }

}