package top.easelink.lcg.ui.search.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_search_lcg.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import top.easelink.framework.topbase.TopActivity
import top.easelink.framework.utils.addFragmentInActivity
import top.easelink.lcg.R
import top.easelink.lcg.config.AppConfig
import top.easelink.lcg.mta.EVENT_OPEN_ARTICLE
import top.easelink.lcg.mta.sendEvent
import top.easelink.lcg.ui.main.article.view.ArticleFragment
import top.easelink.lcg.ui.search.model.OpenSearchResultEvent
import top.easelink.lcg.ui.search.viewmodel.LCGSearchResultAdapter
import top.easelink.lcg.ui.search.viewmodel.LCGSearchViewModel
import top.easelink.lcg.ui.webview.view.WebViewActivity
import top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL
import top.easelink.lcg.utils.showMessage


class LCGSearchActivity : TopActivity() {

    companion object {
        const val KEY_WORD = "key_word"
    }
    private lateinit var mSearchViewModel: LCGSearchViewModel
    private val threadRegex by lazy { Regex("thread-[0-9]+-[0-9]+-[0-9]+.html$", RegexOption.IGNORE_CASE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_lcg)
        EventBus.getDefault().register(this)
        mSearchViewModel = ViewModelProvider(this)[LCGSearchViewModel::class.java]
        setUp()
        val kw = intent.getStringExtra(KEY_WORD)
        if (kw.isNotBlank()) {
            mSearchViewModel.setKeyword(kw)
        }
    }

    override fun onResume() {
        super.onResume()
        if (toolbar.visibility != View.VISIBLE) {
            toolbar.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onBackPressed() {
        if (mFragmentTags.isNotEmpty() && mFragmentTags.size > 1) {
            while (onFragmentDetached(mFragmentTags.pop())) {
                return
            }
        }
        super.onBackPressed()
    }

    private fun setUp() {
        setupRecyclerView()
        setupObserver()
    }

    private fun setupRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@LCGSearchActivity).also {
                it.orientation = RecyclerView.VERTICAL
            }
            itemAnimator = DefaultItemAnimator()
            adapter = LCGSearchResultAdapter(mSearchViewModel)
        }
    }

    private fun setupObserver() {
        mSearchViewModel.totalResult.observe(this, Observer {
            toolbar.title = it
        })
        mSearchViewModel.searchResults.observe(this@LCGSearchActivity, Observer {
            (recycler_view.adapter as? LCGSearchResultAdapter)?.apply {
                clearItems()
                addItems(it)
            }
        })
        mSearchViewModel.isLoading.observe(this@LCGSearchActivity, Observer {
            if (it) {
                searching_file.visibility = View.VISIBLE
                recycler_view.visibility = View.GONE
            } else {
                searching_file.visibility = View.GONE
                recycler_view.visibility = View.VISIBLE
            }
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: OpenSearchResultEvent) {
        sendEvent(EVENT_OPEN_ARTICLE)
        if (threadRegex.containsMatchIn(event.url)) {
            if (AppConfig.searchResultShowInWebView) {
                WebViewActivity.startWebViewWith(SERVER_BASE_URL + event.url, this)
            } else {
                showFragment(ArticleFragment(event.url))
            }
        } else if (event.url.startsWith("http") || event.url.startsWith(SERVER_BASE_URL)){
            WebViewActivity.startWebViewWith(event.url, this)
        } else {
            showMessage(R.string.general_error)
        }
    }

    private fun showFragment(fragment: Fragment) {
        addFragmentInActivity(
            supportFragmentManager,
            fragment,
            R.id.view_root
        )
        toolbar.visibility = View.GONE
    }
}
