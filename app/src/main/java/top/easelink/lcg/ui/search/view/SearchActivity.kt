package top.easelink.lcg.ui.search.view

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import top.easelink.framework.base.BaseActivity
import top.easelink.lcg.BR
import top.easelink.lcg.R
import top.easelink.lcg.databinding.ActivitySearchBinding
import top.easelink.lcg.ui.search.model.OpenSearchResultEvent
import top.easelink.lcg.ui.search.viewmodel.SearchResultAdapter
import top.easelink.lcg.ui.search.viewmodel.SearchResultAdapter.SearchAdapterListener
import top.easelink.lcg.ui.search.viewmodel.SearchViewModel
import top.easelink.lcg.ui.webview.view.WebViewActivity
import top.easelink.lcg.utils.WebsiteConstant

class SearchActivity : BaseActivity<ActivitySearchBinding, SearchViewModel>() {

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_search
    }

    override fun getViewModel(): SearchViewModel {
        return ViewModelProviders.of(this).get(SearchViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        setUp()
        viewModel.initUrl(intent.getStringExtra(WebsiteConstant.URL_KEY))
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun setUp() {
        viewModel.mTotalResult.observe(this@SearchActivity, Observer {
            viewDataBinding.totalInfo.text = it.orEmpty()
        })
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        viewDataBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity).also {
                it.orientation = RecyclerView.VERTICAL
            }
            itemAnimator = DefaultItemAnimator()
            adapter =  SearchResultAdapter(viewModel)
            viewModel.searchResults.observe(this@SearchActivity, Observer {
                (adapter as? SearchResultAdapter)?.apply {
                    clearItems()
                    addItems(it)
                }
            })
        }
        viewDataBinding.refreshLayout.apply {
            setColorSchemeColors(
                ContextCompat.getColor(this@SearchActivity, R.color.colorPrimary),
                ContextCompat.getColor(this@SearchActivity, R.color.colorAccent),
                ContextCompat.getColor(this@SearchActivity, R.color.colorPrimaryDark)
            )
            setScrollUpChild(viewDataBinding!!.recyclerView)
            // Set the scrolling view in the custom SwipeRefreshLayout.
            setOnRefreshListener {
                viewModel.doSearchQuery(SearchAdapterListener.FETCH_INIT)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: OpenSearchResultEvent) {
        WebViewActivity.startWebViewWith(event.searchResult.url, this)
    }
}
