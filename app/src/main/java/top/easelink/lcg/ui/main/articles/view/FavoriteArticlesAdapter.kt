package top.easelink.lcg.ui.main.articles.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_favorite_article_empty_view.view.*
import kotlinx.android.synthetic.main.item_favorite_article_view_v2.view.*
import kotlinx.android.synthetic.main.item_load_more_view.view.*
import org.greenrobot.eventbus.EventBus
import top.easelink.framework.base.BaseViewHolder
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.articles.viewmodel.ArticleFetcher
import top.easelink.lcg.ui.main.articles.viewmodel.FavoriteArticlesViewModel
import top.easelink.lcg.ui.main.model.OpenArticleEvent
import top.easelink.lcg.ui.main.source.model.ArticleEntity
import top.easelink.lcg.utils.getDateFrom


class FavoriteArticlesAdapter(private var favoriteArticlesViewModel: FavoriteArticlesViewModel) :
    RecyclerView.Adapter<BaseViewHolder>(), onMoveAndSwipedListener {
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
                    R.layout.item_favorite_article_view_v2, parent, false
                )
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
            else -> throw IllegalStateException()
        }
    }

    fun addItems(articleEntityList: List<ArticleEntity>) {
        mArticleEntities.addAll(articleEntityList)
        notifyDataSetChanged()
    }

    fun clearItems() {
        mArticleEntities.clear()
    }

    inner class ArticleViewHolder internal constructor(view: View) : BaseViewHolder(view) {

        private fun onItemClick(url: String) {
            val event = OpenArticleEvent(url)
            EventBus.getDefault().post(event)
        }

        override fun onBind(position: Int) {
            val articleEntity = mArticleEntities[position]
            itemView.run {
                startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.recycler_item_show
                    )
                )
                favorite_container.setOnClickListener {
                    onItemClick(articleEntity.url)
                }
                title_text_view.text = articleEntity.title

                date_time.text = getDateFrom(articleEntity.timestamp)

                if (articleEntity.author.isNotBlank()) {
                    author_text_view.apply {
                        text = articleEntity.author
                        visibility = View.VISIBLE
                    }
                } else {
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
        const val VIEW_TYPE_EMPTY = 0
        const val VIEW_TYPE_NORMAL = 1
        const val VIEW_TYPE_LOAD_MORE = 2
    }

    override fun onItemRemove(position: Int) {

    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        return false
    }
}