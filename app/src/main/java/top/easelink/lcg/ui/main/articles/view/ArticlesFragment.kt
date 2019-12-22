package top.easelink.lcg.ui.main.articles.view

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_articles.*
import top.easelink.framework.base.BaseFragment
import top.easelink.lcg.BR
import top.easelink.lcg.LCGApp
import top.easelink.lcg.R
import top.easelink.lcg.databinding.FragmentArticlesBinding
import top.easelink.lcg.ui.main.articles.viewmodel.ArticleFetcher
import top.easelink.lcg.ui.main.articles.viewmodel.ArticlesAdapter
import top.easelink.lcg.ui.main.articles.viewmodel.ArticlesViewModel

class ArticlesFragment : BaseFragment<FragmentArticlesBinding, ArticlesViewModel>() {

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_articles
    }

    override fun getViewModel(): ArticlesViewModel {
        return ViewModelProviders.of(this).get(ArticlesViewModel::class.java)
    }

    private fun scrollToTop() {
        viewDataBinding.backToTop.playAnimation()
        viewDataBinding.recyclerView.let {
            val pos = (it.layoutManager as? LinearLayoutManager)?.findLastCompletelyVisibleItemPosition()
            if (pos != null && pos > 30) {
                it.scrollToPosition(30)
                it.smoothScrollToPosition(0)

            } else {
                it.smoothScrollToPosition(0)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    private fun setUpView() {
        setupRecyclerView()
        arguments?.getString(ARG_PARAM)?.let {
            viewModel.articles.observe(this, Observer {articleList ->
                if (articleList.isEmpty() && viewModel.isLoading.value == true) {
                    viewDataBinding.recyclerView.visibility = View.GONE
                } else {
                    viewDataBinding.recyclerView.visibility = View.VISIBLE
                    (viewDataBinding.recyclerView.adapter as? ArticlesAdapter)?.apply {
                        if (itemCount > 1) {
                            appendItems(articleList)
                        } else {
                            clearItems()
                            addItems(articleList)
                        }
                    }
                }
            })
            viewModel.initUrl(it)
        }
        back_to_top.setOnClickListener {
            scrollToTop()
        }
        viewDataBinding.refreshLayout.run {
            val context = context?:LCGApp.getContext()
            setColorSchemeColors(
                ContextCompat.getColor(context, R.color.colorPrimary),
                ContextCompat.getColor(context, R.color.colorAccent),
                ContextCompat.getColor(context, R.color.colorPrimaryDark)
            )
            // Set the scrolling view in the custom SwipeRefreshLayout.
            setScrollUpChild(viewDataBinding.recyclerView)
            setOnRefreshListener {
                viewModel.fetchArticles(ArticleFetcher.FetchType.FETCH_INIT)
            }
        }
    }

    private fun setupRecyclerView() {
        viewDataBinding?.recyclerView?.apply {
            val mLayoutManager = LinearLayoutManager(context).also {
                it.orientation = RecyclerView.VERTICAL
            }
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = ArticlesAdapter(viewModel).also {
                it.setFragmentManager(fragmentManager?:baseActivity.supportFragmentManager)
            }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(
                    recyclerView: RecyclerView,
                    newState: Int
                ) {
                    viewDataBinding.backToTop.let {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            if (mLayoutManager.findFirstVisibleItemPosition() <= 1) {
                                it.visibility = View.GONE
                                it.pauseAnimation()
                            } else {
                                it.visibility = View.VISIBLE
                            }
                        } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                            it.visibility = View.GONE
                            it.pauseAnimation()
                        }
                    }

                }
            })
        }

    }

    companion object {
        private const val ARG_PARAM = "param"
        @JvmStatic
        fun newInstance(param: String): ArticlesFragment {
            return ArticlesFragment().apply {
                arguments = Bundle().also {
                    it.putString(ARG_PARAM, param)
                }
            }
        }
    }
}