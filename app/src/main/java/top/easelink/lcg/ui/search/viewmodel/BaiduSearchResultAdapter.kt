package top.easelink.lcg.ui.search.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import top.easelink.framework.base.BaseViewHolder
import top.easelink.lcg.R
import top.easelink.lcg.ui.search.model.BaiduSearchResult
import top.easelink.lcg.ui.search.model.OpenBaiduSearchResultEvent
import top.easelink.lcg.ui.search.viewmodel.BaiduSearchResultAdapter.SearchAdapterListener.Companion.FETCH_MORE
import java.util.*

class BaiduSearchResultAdapter(private var mListener: SearchAdapterListener) :
    RecyclerView.Adapter<BaseViewHolder>() {
    private val mBaiduSearchResults: MutableList<BaiduSearchResult> = ArrayList()

    override fun getItemCount(): Int {
        return if (mBaiduSearchResults.isEmpty()) {
            1
        } else {
            mBaiduSearchResults.size + 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mBaiduSearchResults.isEmpty()) {
            VIEW_TYPE_EMPTY
        } else {
            if (position == mBaiduSearchResults.size) {
                VIEW_TYPE_LOAD_MORE
            } else VIEW_TYPE_NORMAL
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> SearchResultViewHolder(LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_search_result_view
                    , parent, false
                ))
            VIEW_TYPE_LOAD_MORE -> LoadMoreViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_search_load_more_view
                        , parent, false
                    )
            )
            VIEW_TYPE_EMPTY -> EmptyViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_empty_view, parent, false)
            )
            else -> EmptyViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_empty_view, parent, false)
            )
        }
    }

    fun addItems(baiduSearchResults: List<BaiduSearchResult>) {
        mBaiduSearchResults.addAll(baiduSearchResults)
        notifyDataSetChanged()
    }

    fun clearItems() {
        mBaiduSearchResults.clear()
    }

    interface SearchAdapterListener {
        fun doSearchQuery(type: Int)

        companion object {
            const val FETCH_INIT = 0
            const val FETCH_MORE = 1
        }
    }

    inner class SearchResultViewHolder internal constructor(val view: View) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {
            val searchResult = mBaiduSearchResults[position]
            view.apply {
                findViewById<View>(R.id.item_search_result).setOnClickListener {
                    EventBus.getDefault().post(OpenBaiduSearchResultEvent(searchResult))
                }
                findViewById<TextView>(R.id.content_text_view).text = searchResult.contentAbstract
                findViewById<TextView>(R.id.title_text_view).text = searchResult.title
            }
        }

    }

    inner class EmptyViewHolder internal constructor(view: View) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {}
    }

    inner class LoadMoreViewHolder internal constructor(view: View) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {
            mListener.doSearchQuery(FETCH_MORE)
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_NORMAL = 1
        private const val VIEW_TYPE_LOAD_MORE = 2
    }

}