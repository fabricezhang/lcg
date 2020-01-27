package top.easelink.lcg.ui.search.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.easelink.lcg.ui.search.model.SearchResult
import top.easelink.lcg.ui.search.source.SearchService.doSearchRequest
import top.easelink.lcg.ui.search.viewmodel.SearchResultAdapter.SearchAdapterListener
import top.easelink.lcg.utils.WebsiteConstant

class SearchViewModel : ViewModel(), SearchAdapterListener {
    val searchResults = MutableLiveData<List<SearchResult>>()
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
            type == SearchAdapterListener.FETCH_MORE && mNextPageUrl?.isNotBlank()?:false-> {
                WebsiteConstant.BAIDU_SEARCH_BASE_URL + mNextPageUrl
            }
            type == SearchAdapterListener.FETCH_INIT -> {
                mUrl!!
            }
            else ->  return
        }
        GlobalScope.launch(Dispatchers.IO){
            doSearchRequest(requestUrl).apply {
                if (searchResultList.isNotEmpty()) {
                    val list = searchResults.value
                    mNextPageUrl = nextPageUrl
                    mTotalResult.postValue(totalResult)
                    if (type == SearchAdapterListener.FETCH_MORE && list != null && list.isNotEmpty()) {
                        searchResults.postValue(list.plus(searchResultList))
                    } else {
                        searchResults.postValue(searchResultList)
                    }
                }
            }
            isLoading.postValue(false)
        }

    }
}