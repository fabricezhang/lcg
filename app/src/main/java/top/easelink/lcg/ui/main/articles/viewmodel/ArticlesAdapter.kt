package top.easelink.lcg.ui.main.articles.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import top.easelink.framework.base.BaseViewHolder
import top.easelink.lcg.R
import top.easelink.lcg.databinding.ItemArticleEmptyViewBinding
import top.easelink.lcg.databinding.ItemArticleViewBinding
import top.easelink.lcg.ui.main.article.view.PostPreviewDialog
import top.easelink.lcg.ui.main.articles.viewmodel.ArticleEmptyItemViewModel.ArticleEmptyItemViewModelListener
import top.easelink.lcg.ui.main.source.model.Article
import java.lang.ref.WeakReference

class ArticlesAdapter(
    private var mListener: ArticleFetcher?
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var fragmentManager: WeakReference<FragmentManager>? = null
    private val mArticleList: MutableList<Article> = mutableListOf()

    override fun getItemCount(): Int {
        return if (mArticleList.isEmpty()) {
            1
        } else {
            mArticleList.size + 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mArticleList.isEmpty()) {
            VIEW_TYPE_EMPTY
        } else {
            if (position == mArticleList.size) {
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
                val articleViewBinding = ItemArticleViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                ArticleViewHolder(articleViewBinding)
            }
            VIEW_TYPE_LOAD_MORE -> LoadMoreViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_load_more_view
                        , parent, false
                    )
            )
            VIEW_TYPE_EMPTY -> {
                val emptyViewBinding =
                    ItemArticleEmptyViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                    )
                EmptyViewHolder(
                    emptyViewBinding
                )
            }
            else -> {
                val emptyViewBinding =
                    ItemArticleEmptyViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                    )
                EmptyViewHolder(
                    emptyViewBinding
                )
            }
        }
    }

    fun addItems(articleList: List<Article>) {
        mArticleList.addAll(articleList)
        notifyDataSetChanged()
    }

    fun clearItems() {
        mArticleList.clear()
    }

    fun setFragmentManager(fragmentManager: FragmentManager) {
        this.fragmentManager = WeakReference(fragmentManager)
    }

    inner class ArticleViewHolder internal constructor(private val mBinding: ItemArticleViewBinding) :
        BaseViewHolder(mBinding.root) {
        private var articleItemViewModel: ArticleItemViewModel? = null
        override fun onBind(position: Int) {
            mBinding.titleTextView.setOnLongClickListener {
                fragmentManager?.get()?.let {
                    PostPreviewDialog
                        .newInstance(mArticleList[position].url)
                        .show(it, PostPreviewDialog.TAG)
                }
                true
            }
            val article = mArticleList[position]
            articleItemViewModel = ArticleItemViewModel(article)
            mBinding.viewModel = articleItemViewModel
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }

    }

    inner class EmptyViewHolder internal constructor(private val mBinding: ItemArticleEmptyViewBinding) :
        BaseViewHolder(mBinding.root), ArticleEmptyItemViewModelListener {
        override fun onBind(position: Int) {
            val emptyItemViewModel = ArticleEmptyItemViewModel(this)
            mBinding.viewModel = emptyItemViewModel
        }

        override fun onRetryClick() {
            mListener?.fetchArticles(ArticleFetcher.FETCH_INIT)
        }

    }

    inner class LoadMoreViewHolder internal constructor(view: View?) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {
            mListener?.fetchArticles(ArticleFetcher.FETCH_MORE)
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_NORMAL = 1
        private const val VIEW_TYPE_LOAD_MORE = 2
    }

}