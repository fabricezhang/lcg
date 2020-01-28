package top.easelink.lcg.ui.main.articles.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_favorite_article_empty_view.view.*
import kotlinx.android.synthetic.main.item_favorite_article_view.view.*
import kotlinx.android.synthetic.main.item_load_more_view.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import top.easelink.framework.base.BaseViewHolder
import top.easelink.framework.threadpool.CommonPool
import top.easelink.framework.threadpool.Main
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.model.OpenArticleEvent
import top.easelink.lcg.ui.main.source.local.ArticlesLocalDataSource.delArticleFromFavorite
import top.easelink.lcg.ui.main.source.model.ArticleEntity

class FavoriteArticlesAdapter(private var favoriteArticlesViewModel: FavoriteArticlesViewModel) :
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
            VIEW_TYPE_NORMAL -> ArticleViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_favorite_article_view, parent, false)
            )
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

    inner class ArticleViewHolder internal constructor(private val view: View) : BaseViewHolder(view) {

        // will be assigned in onBind(), should not be called before onBind()
        private lateinit var articleEntity: ArticleEntity

        private fun onItemClick() {
            val event = OpenArticleEvent(articleEntity.url)
            EventBus.getDefault().post(event)
        }

        override fun onBind(position: Int) {
            articleEntity = mArticleEntities[position].also {entity ->
                view.remove_button.setOnClickListener {
                    GlobalScope.launch(CommonPool) {
                        //TODO del url has a confirmation button to click, support this in the future
//                        if (entity.delUrl.isNotEmpty()) {
//                            val doc = Client.sendGetRequestWithQuery(entity.delUrl)
//                        }
                        if (delArticleFromFavorite(entity.id)) {
                            mArticleEntities.remove(entity)
                            GlobalScope.launch(Main) {
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, mArticleEntities.size - position)
                            }
                        }
                    }
                }
            }
            view.run {
                favorite_container.apply {
                    when {
                        // odd
                        position % 2 == 0 -> {
                            setBackgroundResource(R.drawable.rectangle_bg_slightly_light_gray_4dp)
                        }
                        // even
                        else -> {
                            setBackgroundResource(R.drawable.rectangle_bg_white_4dp)
                        }
                    }
                }.setOnClickListener {
                    onItemClick()
                }
                title_text_view.text = articleEntity.title
                if (articleEntity.author.isNotBlank()) {
                    author_text_view.apply {
                        text = articleEntity.author
                        visibility = View.VISIBLE
                    }
                }  else {
                    author_text_view.visibility = View.GONE
                }
            }
        }

    }

    inner class EmptyViewHolder internal constructor(val view: View) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {
            view.sync_favorites.setOnClickListener {
                favoriteArticlesViewModel.syncFavorites()
            }
        }
    }

    inner class LoadMoreViewHolder internal constructor(val view: View) : BaseViewHolder(view) {
        override fun onBind(position: Int) {
            view.loading.visibility = View.VISIBLE
            favoriteArticlesViewModel.fetchArticles(ArticleFetcher.FetchType.FETCH_MORE) {
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