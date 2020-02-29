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
import kotlinx.android.synthetic.main.fragment_forum_articles.*
import timber.log.Timber
import top.easelink.framework.base.BaseFragment
import top.easelink.lcg.BR
import top.easelink.lcg.LCGApp
import top.easelink.lcg.R
import top.easelink.lcg.databinding.FragmentForumArticlesBinding
import top.easelink.lcg.mta.CHANGE_THREAD
import top.easelink.lcg.ui.main.articles.viewmodel.*
import top.easelink.lcg.ui.main.source.model.ForumThread

class ForumArticlesFragment : BaseFragment<FragmentForumArticlesBinding, ForumArticlesViewModel>() {

    private var showTab = false

    override fun isControllable(): Boolean {
        return true
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_forum_articles
    }

    override fun getViewModel(): ForumArticlesViewModel {
        return ViewModelProviders.of(this).get(ForumArticlesViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.run {
            showTab = getBoolean(ARG_SHOW_TAB, true)
        }
        setUpToolbar()
        setUp()
    }

    private fun setUpToolbar() {
        article_toolbar.apply {
            inflateMenu(R.menu.forum_articles)
            setOnMenuItemClickListener { menuItem ->
                val order = when (menuItem.itemId) {
                    R.id.action_order_by_datetime -> DATE_LINE_ORDER
                    R.id.action_order_by_lastpost -> LAST_POST_ORDER
                    else -> DEFAULT_ORDER
                }
                try {
                    val pos = viewDataBinding.forumTab.selectedTabPosition
                    viewModel
                        .threadList
                        .value
                        ?.get(pos)
                        ?.threadUrl
                        ?.let {
                            viewModel.initUrlAndFetch(
                                url = it,
                                fetchType = ArticleFetcher.FetchType.FETCH_INIT,
                                order = order
                            )
                        }
                } catch (e: Exception) {
                    Timber.e(e)
                }
                return@setOnMenuItemClickListener true
            }
        }
    }


    private fun setUp() {
        viewModel.threadList.observe(viewLifecycleOwner, Observer {
                threadList -> setUpTabLayout(threadList)
        })
        arguments?.let {
            viewModel.initUrlAndFetch(
                url = it.getString(ARG_PARAM)!!,
                fetchType = ArticleFetcher.FetchType.FETCH_INIT)
            viewModel.setTitle(it.getString(ARG_TITLE).orEmpty())
        }
        setUpRecyclerView()
    }

    private fun setUpTabLayout(forumThreadList: List<ForumThread>?) {
        // hide tab if showTab has been set to false or there's no tab item
        if (forumThreadList.isNullOrEmpty() || !showTab) {
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
                    viewModel.initUrlAndFetch(
                        url = forumThreadList[tab.position].threadUrl,
                        fetchType = ArticleFetcher.FetchType.FETCH_INIT
                    )
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }
    }

    private fun setUpRecyclerView() {
        viewDataBinding.refreshLayout.apply {
            LCGApp.context.let {
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
                    adapter = ArticlesAdapter(viewModel).also {
                        it.setFragmentManager(fragmentManager?:baseActivity.supportFragmentManager)
                    }
                }
            )
            setOnRefreshListener {
                viewModel.fetchArticles(ArticleFetcher.FetchType.FETCH_INIT){}
            }
        }
        // Add articles observer
        viewModel.articles.observe(viewLifecycleOwner, Observer { articleList ->
            if (articleList.isEmpty() && viewModel.isLoading.value == true) {
                viewDataBinding.recyclerView.visibility = View.GONE
            } else {
                viewDataBinding.recyclerView.visibility = View.VISIBLE
                (viewDataBinding.recyclerView.adapter as? ArticlesAdapter)?.apply {
                    // a workaround for distinguish fetch_init / fetch_more / thread
                    clearItems()
                    addItems(articleList)
                }
            }
        })
    }

    companion object {
        private const val ARG_PARAM = "url"
        private const val ARG_TITLE = "title"
        private const val ARG_SHOW_TAB = "showTab"
        @JvmStatic
        fun newInstance(title: String, param: String, showTab: Boolean = true): ForumArticlesFragment {
            val args = Bundle()
            args.putString(ARG_PARAM, param)
            args.putString(ARG_TITLE, title)
            args.putBoolean(ARG_SHOW_TAB, showTab)
            val fragment = ForumArticlesFragment()
            fragment.arguments = args
            return fragment
        }
    }
}