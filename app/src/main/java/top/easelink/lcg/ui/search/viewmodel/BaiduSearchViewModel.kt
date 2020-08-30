package top.easelink.lcg.ui.search.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.easelink.framework.threadpool.IOPool
import top.easelink.lcg.ui.search.model.BaiduSearchResult
import top.easelink.lcg.ui.search.source.BaiduSearchService.doSearchRequest
import top.easelink.lcg.ui.search.viewmodel.BaiduSearchResultAdapter.SearchAdapterListener
import top.easelink.lcg.utils.WebsiteConstant

class BaiduSearchViewModel : ViewModel(), SearchAdapterListener {
    val searchResults = MutableLiveData<List<BaiduSearchResult>>()
    val mTotalResult = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()
    private var mUrl: String? = null
    private var mNextPageUrl: String? = null

    fun initUrl(url: String?) {
        mUrl = url
        doSearchQuery(SearchAdapterListener.FETCH_INIT)
    }

    override fun doSearchQuery(type: Int) {
        isLoading.value = true
        val requestUrl: String = when {
            type == SearchAdapterListener.FETCH_MORE && mNextPageUrl?.isNotBlank() ?: false -> {
                WebsiteConstant.BAIDU_SEARCH_BASE_URL + mNextPageUrl
            }
            type == SearchAdapterListener.FETCH_INIT -> {
                mUrl!!
            }
            else -> return
        }
        GlobalScope.launch(IOPool) {
            doSearchRequest(requestUrl, 0).apply {
                if (baiduSearchResultList.isNotEmpty()) {
                    val list = searchResults.value
                    mNextPageUrl = nextPageUrl
                    mTotalResult.postValue(totalResult)
                    if (type == SearchAdapterListener.FETCH_MORE && list != null && list.isNotEmpty()) {
                        searchResults.postValue(list.plus(baiduSearchResultList))
                    } else {
                        searchResults.postValue(baiduSearchResultList)
                    }
                }
                isLoading.postValue(false)
            }
        }

    }
}