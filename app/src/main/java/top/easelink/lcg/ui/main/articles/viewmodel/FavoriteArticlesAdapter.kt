package top.easelink.lcg.ui.main.articles.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import top.easelink.framework.base.BaseViewHolder
import top.easelink.lcg.R
import top.easelink.lcg.databinding.ItemFavoriteArticleViewBinding
import top.easelink.lcg.ui.main.source.local.ArticlesLocalDataSource.delArticleFromFavorite
import top.easelink.lcg.ui.main.source.model.ArticleEntity

class FavoriteArticlesAdapter(private var mListener: ArticleFetcher) :
    RecyclerView.Adapter<BaseViewHolder>() {
    private val mArticleEntities: MutableList<ArticleEntity> = mutableListOf()

    override fun getItemCount(): Int {
        return if (mArticleEntities.isEmpty()) {
            1
        } else {
            mArticleEntities.size + 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mArticleEntities.isEmpty()) {
            VIEW_TYPE_EMPTY
        } else {
            if (position == mArticleEntities.size) {
                VIEW_TYPE_LOAD_MORE
            } else {
                VIEW_TYPE_NORMAL
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> {
                val favoriteArticleViewBinding = ItemFavoriteArticleViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                ArticleViewHolder(favoriteArticleViewBinding, this)
            }
            VIEW_TYPE_LOAD_MORE -> LoadMoreViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_load_more_view, parent, false
                )
            )
            VIEW_TYPE_EMPTY -> EmptyViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_favorite_article_empty_view, parent, false
                )
            )
            else -> EmptyViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_favorite_article_empty_view, parent, false
                )
            )
        }
    }

    fun addItems(articleEntityList: List<ArticleEntity>) {
        mArticleEntities.addAll(articleEntityList)
        notifyDataSetChanged()
    }

    fun clearItems() {
        mArticleEntities.clear()
    }

    inner class ArticleViewHolder internal constructor(
        private val mBinding: ItemFavoriteArticleViewBinding,
        private val mAdapter: FavoriteArticlesAdapter
    ) : BaseViewHolder(mBinding.root) {
        private var favoriteArticleItemViewModel: FavoriteArticleItemViewModel? = null

        override fun onBind(position: Int) {
            val articleEntity = mArticleEntities[position]
            mBinding.removeButton.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    if (delArticleFromFavorite(articleEntity.id)) {
                        mArticleEntities.remove(articleEntity)
                        GlobalScope.launch(Dispatchers.Main) {
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, mArticleEntities.size - position)
                        }
                    }
                }
            }
            favoriteArticleItemViewModel = FavoriteArticleItemViewModel(articleEntity)
            try {
                mBinding.contentTextView.setHtml(articleEntity.content.trim { it <= ' ' })
            } catch (e: Exception) {
                Timber.e(e)
            }
            mBinding.viewModel = favoriteArticleItemViewModel
            mBinding.executePendingBindings()
        }

    }

    inner class EmptyViewHolder internal constructor(view: View?) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {}
    }

    inner class LoadMoreViewHolder internal constructor(view: View?) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {
            mListener.fetchArticles(ArticleFetcher.FETCH_MORE)
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_NORMAL = 1
        private const val VIEW_TYPE_LOAD_MORE = 2
    }
}