package top.easelink.lcg.ui.search.viewmodel

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_lcg_search_result_view.view.*
import kotlinx.android.synthetic.main.item_load_more_view.view.*
import org.greenrobot.eventbus.EventBus
import top.easelink.framework.base.BaseViewHolder
import top.easelink.lcg.R
import top.easelink.lcg.ui.search.model.LCGSearchResultItem
import top.easelink.lcg.ui.search.model.OpenSearchResultEvent

class LCGSearchResultAdapter(
    private var mFetcher: ContentFetcher
) : RecyclerView.Adapter<BaseViewHolder>() {
    private val mSearchResults: MutableList<LCGSearchResultItem> = mutableListOf()

    override fun getItemCount(): Int {
        return if (mSearchResults.isEmpty()) {
            1
        } else {
            mSearchResults.size + 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mSearchResults.isEmpty()) {
            VIEW_TYPE_EMPTY
        } else {
            if (position == mSearchResults.size) {
                VIEW_TYPE_LOAD_MORE
            } else VIEW_TYPE_NORMAL
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> SearchResultViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_lcg_search_result_view, parent, false
                    )
            )
            VIEW_TYPE_LOAD_MORE -> LoadMoreViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_load_more_view, parent, false
                    )
            )
            //VIEW_TYPE_EMPTY
            else -> EmptyViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_empty_view, parent, false)
            )
        }
    }

    fun addItems(LCGSearchResults: List<LCGSearchResultItem>) {
        mSearchResults.addAll(LCGSearchResults)
        notifyDataSetChanged()
    }

    fun clearItems() {
        mSearchResults.clear()
    }

    interface ContentFetcher {
        fun fetch(type: Type, callback: ((Boolean) -> Unit)?)

        enum class Type {
            INIT,
            NEXT_PAGE
        }
    }

    private inner class SearchResultViewHolder(view: View) : BaseViewHolder(view) {
        override fun onBind(position: Int) {
            val searchResult = mSearchResults[position]
            itemView.apply {
                title_tv.text = Html.fromHtml(searchResult.title)
                author_tv.text = searchResult.author
                reply_and_view_tv.text = searchResult.replyView
                date_tv.text = searchResult.date
                forum_tv.text = searchResult.forum
                content_tv.text = Html.fromHtml(searchResult.content)
                setOnClickListener {
                    EventBus.getDefault().post(OpenSearchResultEvent(searchResult.url))
                }
            }
        }

    }

    private inner class EmptyViewHolder(view: View) : BaseViewHolder(view) {
        override fun onBind(position: Int) {}
    }

    private inner class LoadMoreViewHolder(view: View) : BaseViewHolder(view) {
        override fun onBind(position: Int) {
            mFetcher.fetch(ContentFetcher.Type.NEXT_PAGE) { success ->
                itemView.post {
                    itemView.loading.visibility = if (success) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
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