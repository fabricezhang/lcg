package top.easelink.lcg.ui.main.articles.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_article_view.view.*
import kotlinx.android.synthetic.main.item_load_more_view.view.*
import org.greenrobot.eventbus.EventBus
import top.easelink.framework.base.BaseViewHolder
import top.easelink.lcg.R
import top.easelink.lcg.databinding.ItemArticleEmptyViewBinding
import top.easelink.lcg.spipedata.UserData
import top.easelink.lcg.ui.main.article.view.PostPreviewDialog
import top.easelink.lcg.ui.main.articles.viewmodel.ArticleEmptyItemViewModel
import top.easelink.lcg.ui.main.articles.viewmodel.ArticleEmptyItemViewModel.ArticleEmptyItemViewModelListener
import top.easelink.lcg.ui.main.articles.viewmodel.ArticleFetcher
import top.easelink.lcg.ui.main.model.OpenArticleEvent
import top.easelink.lcg.ui.main.source.model.Article
import java.lang.ref.WeakReference

class ArticlesAdapter(
    private var articleFetcher: ArticleFetcher
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var fragmentManager: WeakReference<FragmentManager>? = null
    private val mArticleList: MutableList<Article> = mutableListOf()

    override fun getItemCount(): Int {
        return when {
            mArticleList.isEmpty() -> 1
            // for articles more than 10, add a load more
            mArticleList.size > 10 -> mArticleList.size + 1
            else -> mArticleList.size
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
            VIEW_TYPE_NORMAL -> { ArticleViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_article_view
                        , parent, false
                    ))
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

    fun appendItems(notifications: List<Article>) {
        val count = itemCount
        mArticleList.addAll(notifications)
        notifyItemRangeInserted(count - 1, notifications.size)
    }

    fun clearItems() {
        mArticleList.clear()
    }

    fun setFragmentManager(fragmentManager: FragmentManager) {
        this.fragmentManager = WeakReference(fragmentManager)
    }

    inner class ArticleViewHolder internal constructor(private val view: View) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {
            val article = mArticleList[position]
            view.layout.apply {
                setOnLongClickListener {
                    fragmentManager?.get()?.let {
                        PostPreviewDialog.newInstance(mArticleList[position].url)
                            .show(it, PostPreviewDialog.TAG)
                    }
                    true
                }
                setOnClickListener {
                    EventBus.getDefault().post(OpenArticleEvent(article.url))
                }
//                if (article.author == UserData.username) {
//                    layout.strokeColor = ContextCompat.getColor(context, R.color.google)
//                    layout.strokeWidth = 1.dpToPx(context).toInt()
//                } else {
//                    layout.strokeWidth = 0
//                }
            }
            view.apply {
                title_text_view.text = article.title
                author_text_view.text = article.author
                date_text_view.text = article.date
                reply_and_view.text = article.let { "${it.reply} / ${it.view}" }
                origin.text = article.origin
                if (article.author == UserData.username) {
                    stamp.apply {
                        visibility = View.VISIBLE
                        setStampColor(ContextCompat.getColor(context, R.color.orange))
                        setText(context.getString(R.string.my_post))
                        reDraw()
                    }
                } else {
                    when (article.helpCoin) {
                        0 -> stamp.visibility = View.GONE
                        -1 -> stamp.apply {
                            setDrawSpotEnable(true)
                            setStampColor(ContextCompat.getColor(context, R.color.light_gray))
                            setText(context.getString(R.string.help_request_solved))
                            visibility = View.VISIBLE
                            reDraw()
                        }
                        else -> stamp.apply {
                            stamp.setDrawSpotEnable(true)
                            stamp.setStampColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.colorAccent
                                )
                            )
                            stamp.setText(article.helpCoin.toString())
                            stamp.visibility = View.VISIBLE
                            stamp.reDraw()
                        }
                    }
                }
            }
        }
    }

    inner class EmptyViewHolder internal constructor(private val mBinding: ItemArticleEmptyViewBinding) :
        BaseViewHolder(mBinding.root), ArticleEmptyItemViewModelListener {
        override fun onBind(position: Int) {
            val emptyItemViewModel =
                ArticleEmptyItemViewModel(
                    this
                )
            mBinding.viewModel = emptyItemViewModel
        }

        override fun onRetryClick() {
            articleFetcher.fetchArticles(ArticleFetcher.FetchType.FETCH_INIT){}
        }

    }

    inner class LoadMoreViewHolder internal constructor(val view: View) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {
            view.loading.visibility = View.VISIBLE
            articleFetcher.fetchArticles(ArticleFetcher.FetchType.FETCH_MORE){
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