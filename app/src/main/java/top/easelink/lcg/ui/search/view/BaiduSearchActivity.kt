package top.easelink.lcg.ui.search.view

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_baidu_search.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import top.easelink.framework.topbase.TopActivity
import top.easelink.framework.utils.addFragmentInActivity
import top.easelink.lcg.R
import top.easelink.lcg.config.AppConfig.searchResultShowInWebView
import top.easelink.lcg.event.EVENT_OPEN_ARTICLE
import top.easelink.lcg.event.sendEvent
import top.easelink.lcg.ui.main.article.view.ArticleFragment
import top.easelink.lcg.ui.search.model.OpenBaiduSearchResultEvent
import top.easelink.lcg.ui.search.viewmodel.BaiduSearchResultAdapter
import top.easelink.lcg.ui.search.viewmodel.BaiduSearchResultAdapter.SearchAdapterListener
import top.easelink.lcg.ui.search.viewmodel.BaiduSearchViewModel
import top.easelink.lcg.ui.webview.view.WebViewActivity
import top.easelink.lcg.utils.WebsiteConstant.URL_KEY


class BaiduSearchActivity : TopActivity() {

    private lateinit var mViewModelBaidu: BaiduSearchViewModel
    private val threadRegex by lazy {
        Regex(
            "thread-[0-9]+-[0-9]+-[0-9]+.html$",
            RegexOption.IGNORE_CASE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baidu_search)
        EventBus.getDefault().register(this)
        mViewModelBaidu = ViewModelProvider(this)[BaiduSearchViewModel::class.java]
        setUp()
        mViewModelBaidu.initUrl(intent.getStringExtra(URL_KEY))
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun setUp() {
        mViewModelBaidu.mTotalResult.observe(this, Observer {
            total_info.text = it.orEmpty()
        })
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@BaiduSearchActivity).also {
                it.orientation = RecyclerView.VERTICAL
            }
            itemAnimator = DefaultItemAnimator()
            adapter = BaiduSearchResultAdapter(mViewModelBaidu)
            mViewModelBaidu.searchResults.observe(this@BaiduSearchActivity, Observer {
                (adapter as? BaiduSearchResultAdapter)?.apply {
                    clearItems()
                    addItems(it)
                }
            })
        }
        mViewModelBaidu.isLoading.observe(this, Observer {
            refresh_layout.isRefreshing = it
        })
        refresh_layout.apply {
            setColorSchemeColors(
                ContextCompat.getColor(this@BaiduSearchActivity, R.color.colorPrimary),
                ContextCompat.getColor(this@BaiduSearchActivity, R.color.colorAccent),
                ContextCompat.getColor(this@BaiduSearchActivity, R.color.colorPrimaryDark)
            )
            setScrollUpChild(recycler_view)
            // Set the scrolling view in the custom SwipeRefreshLayout.
            setOnRefreshListener {
                mViewModelBaidu.doSearchQuery(SearchAdapterListener.FETCH_INIT)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: OpenBaiduSearchResultEvent) {
        event.baiduSearchResult.url.let { url ->
            if (!searchResultShowInWebView && threadRegex.containsMatchIn(url)) {
                if (url.startsWith("http") || url.startsWith("www")) {
                    openAsArticle(url.substringAfterLast("/"))
                } else {
                    openAsArticle(url)
                }
            } else {
                WebViewActivity.startWebViewWith(url, this)
            }
        }
    }

    private fun openAsArticle(url: String) {
        sendEvent(EVENT_OPEN_ARTICLE)
        showFragment(ArticleFragment.newInstance(url))
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
