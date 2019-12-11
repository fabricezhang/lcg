package top.easelink.lcg.ui.main.articles.view

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.tencent.stat.StatService
import top.easelink.framework.base.BaseFragment
import top.easelink.lcg.BR
import top.easelink.lcg.LCGApp
import top.easelink.lcg.R
import top.easelink.lcg.databinding.FragmentForumArticlesBinding
import top.easelink.lcg.mta.CHANGE_THREAD
import top.easelink.lcg.ui.main.articles.viewmodel.ArticleFetcher
import top.easelink.lcg.ui.main.articles.viewmodel.ArticlesAdapter
import top.easelink.lcg.ui.main.articles.viewmodel.ForumArticlesViewModel
import top.easelink.lcg.ui.main.source.model.ForumThread

class ForumArticlesFragment :
    BaseFragment<FragmentForumArticlesBinding, ForumArticlesViewModel>() {
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_forum_articles
    }

    override fun getViewModel(): ForumArticlesViewModel {
        return ViewModelProviders.of(this).get(ForumArticlesViewModel::class.java)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    private fun setUp() {
        arguments?.let {
            viewModel.threadList.observe(this, Observer {
                    threadList -> setUpTabLayout(threadList)
            })
            viewModel.initUrl(it.getString(ARG_PARAM), ArticleFetcher.FETCH_INIT)
            viewModel.setTitle(it.getString(ARG_TITLE) ?: "PlaceHolder")
        }
        setUpRecyclerView()
    }

    private fun setUpTabLayout(forumThreadList: List<ForumThread>?) {
        if (forumThreadList.isNullOrEmpty()) {
            viewDataBinding.forumTab.apply {
                visibility = View.GONE
                removeAllTabs()
            }
            return
        }
        viewDataBinding.forumTab.apply {
            visibility = View.VISIBLE
            removeAllTabs()
            forumThreadList.forEach {
                addTab(newTab().setText(it.threadName))
            }
            addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    StatService.trackCustomEvent(context, CHANGE_THREAD)
                    viewModel.initUrl(
                        forumThreadList[tab.position].threadUrl,
                        ArticleFetcher.FETCH_BY_THREAD
                    )
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }
    }

    private fun setUpRecyclerView() {
        viewDataBinding.refreshLayout.apply {
            LCGApp.getContext().let {
                setColorSchemeColors(
                    ContextCompat.getColor(it, R.color.colorPrimary),
                    ContextCompat.getColor(it, R.color.colorAccent),
                    ContextCompat.getColor(it, R.color.colorPrimaryDark)
                )
            }
            // Set the scrolling view in the custom SwipeRefreshLayout.
            setScrollUpChild(
                viewDataBinding.recyclerView.apply {
                    layoutManager = LinearLayoutManager(context).apply {
                        orientation = RecyclerView.VERTICAL
                    }
                    itemAnimator = DefaultItemAnimator()
                    adapter = ArticlesAdapter(viewModel)
                }
            )
            setOnRefreshListener {
                viewModel.fetchArticles(ArticleFetcher.FETCH_INIT)
            }
        }
        // Add articles observer
        viewModel.articles.observe(this@ForumArticlesFragment, Observer {
            (viewDataBinding.recyclerView.adapter as? ArticlesAdapter)?.run {
                clearItems()
                addItems(it)
            }
        })
    }

    companion object {
        private const val ARG_PARAM = "url"
        private const val ARG_TITLE = "title"
        @JvmStatic
        fun newInstance(title: String, param: String): ForumArticlesFragment {
            val args = Bundle()
            args.putString(ARG_PARAM, param)
            args.putString(ARG_TITLE, title)
            val fragment = ForumArticlesFragment()
            fragment.arguments = args
            return fragment
        }
    }
}