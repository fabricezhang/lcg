package top.easelink.lcg.ui.main.articles.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_favorite_articles.*
import top.easelink.framework.base.BaseFragment
import top.easelink.lcg.BR
import top.easelink.lcg.R
import top.easelink.lcg.databinding.FragmentFavoriteArticlesBinding
import top.easelink.lcg.ui.main.articles.viewmodel.ArticleFetcher
import top.easelink.lcg.ui.main.articles.viewmodel.FavoriteArticlesAdapter
import top.easelink.lcg.ui.main.articles.viewmodel.FavoriteArticlesViewModel

class FavoriteArticlesFragment : BaseFragment<FragmentFavoriteArticlesBinding, FavoriteArticlesViewModel>() {

    override fun isControllable(): Boolean {
        return true
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_favorite_articles
    }

    override fun getViewModel(): FavoriteArticlesViewModel {
        return ViewModelProviders.of(this).get(FavoriteArticlesViewModel::class.java)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        setupToolBar()
        viewModel.fetchArticles(ArticleFetcher.FetchType.FETCH_INIT){}
    }

    private fun setUpRecyclerView() {
        viewDataBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context).apply {
                orientation = RecyclerView.VERTICAL
            }
            itemAnimator = DefaultItemAnimator()
            adapter = FavoriteArticlesAdapter(viewModel)
        }
        viewModel.articles.observe(this, Observer {
            (viewDataBinding.recyclerView.adapter as? FavoriteArticlesAdapter)?.apply {
                clearItems()
                addItems(it)
            }
        })

    }

    private fun setupToolBar() {
        article_toolbar.apply {
            inflateMenu(R.menu.favorite_articles)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_remove_all -> viewModel.removeAllFavorites()
                    R.id.action_sync_my_favorites -> viewModel.syncFavorites()
                    else -> {
                        // to add mores
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }

    }
}