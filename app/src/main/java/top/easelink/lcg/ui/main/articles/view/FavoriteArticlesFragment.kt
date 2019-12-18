package top.easelink.lcg.ui.main.articles.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import top.easelink.framework.base.BaseFragment
import top.easelink.lcg.BR
import top.easelink.lcg.R
import top.easelink.lcg.databinding.FragmentFavoriteArticlesBinding
import top.easelink.lcg.ui.main.articles.viewmodel.ArticleFetcher
import top.easelink.lcg.ui.main.articles.viewmodel.FavoriteArticlesAdapter
import top.easelink.lcg.ui.main.articles.viewmodel.FavoriteArticlesViewModel

class FavoriteArticlesFragment : BaseFragment<FragmentFavoriteArticlesBinding, FavoriteArticlesViewModel>() {

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
        setupMenu()
        viewModel.fetchArticles(ArticleFetcher.FetchType.FETCH_INIT)
    }

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater
    ) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.favorite_articles, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_remove_all -> viewModel.removeAllFavorites()
            else -> {
                // to add more
            }
        }
        return super.onOptionsItemSelected(item)
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

    private fun setupMenu() {
        setHasOptionsMenu(true)
        (activity as? AppCompatActivity)?.setSupportActionBar(viewDataBinding?.articleToolbar)
    }

    companion object {
        @JvmStatic
        fun newInstance(): FavoriteArticlesFragment {
            return FavoriteArticlesFragment()
        }
    }
}