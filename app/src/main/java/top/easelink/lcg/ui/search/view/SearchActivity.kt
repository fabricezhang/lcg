package top.easelink.lcg.ui.search.view

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_search.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import top.easelink.framework.topbase.TopActivity
import top.easelink.framework.utils.addFragmentInActivity
import top.easelink.lcg.R
import top.easelink.lcg.config.AppConfig.searchResultShowInWebView
import top.easelink.lcg.mta.EVENT_OPEN_ARTICLE
import top.easelink.lcg.mta.sendEvent
import top.easelink.lcg.ui.main.article.view.ArticleFragment
import top.easelink.lcg.ui.search.model.OpenSearchResultEvent
import top.easelink.lcg.ui.search.viewmodel.SearchResultAdapter
import top.easelink.lcg.ui.search.viewmodel.SearchResultAdapter.SearchAdapterListener
import top.easelink.lcg.ui.search.viewmodel.SearchViewModel
import top.easelink.lcg.ui.webview.view.WebViewActivity
import top.easelink.lcg.utils.WebsiteConstant.URL_KEY


class SearchActivity : TopActivity() {

    private lateinit var mViewModel: SearchViewModel
    private val threadRegex by lazy { Regex("thread-[0-9]+-[0-9]+-[0-9]+.html$", RegexOption.IGNORE_CASE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        EventBus.getDefault().register(this)
        mViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        setUp()
        mViewModel.initUrl(intent.getStringExtra(URL_KEY))
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun setUp() {
        mViewModel.mTotalResult.observe(this, Observer {
            total_info.text = it.orEmpty()
        })
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity).also {
                it.orientation = RecyclerView.VERTICAL
            }
            itemAnimator = DefaultItemAnimator()
            adapter =  SearchResultAdapter(mViewModel)
            mViewModel.searchResults.observe(this@SearchActivity, Observer {
                (adapter as? SearchResultAdapter)?.apply {
                    clearItems()
                    addItems(it)
                }
            })
        }
        mViewModel.isLoading.observe(this, Observer {
            refresh_layout.isRefreshing = it
        })
        refresh_layout.apply {
            setColorSchemeColors(
                ContextCompat.getColor(this@SearchActivity, R.color.colorPrimary),
                ContextCompat.getColor(this@SearchActivity, R.color.colorAccent),
                ContextCompat.getColor(this@SearchActivity, R.color.colorPrimaryDark)
            )
            setScrollUpChild(recycler_view)
            // Set the scrolling view in the custom SwipeRefreshLayout.
            setOnRefreshListener {
                mViewModel.doSearchQuery(SearchAdapterListener.FETCH_INIT)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: OpenSearchResultEvent) {
        event.searchResult.url.let {url ->
            if (!searchResultShowInWebView && threadRegex.containsMatchIn(url)) {
                //FIXME not work for the moment, fixed in the future
                throw IllegalStateException("feature not finished")
//                openAsArticle(url)
            } else {
                WebViewActivity.startWebViewWith(url, this)
            }
        }
    }

    private fun openAsArticle(url: String) {
        sendEvent(EVENT_OPEN_ARTICLE)
        showFragment(ArticleFragment(url))
    }

    private fun showFragment(fragment: Fragment) {
        addFragmentInActivity(
            supportFragmentManager,
            fragment,
            R.id.view_root,
            addToBack = false
        )
    }
}
