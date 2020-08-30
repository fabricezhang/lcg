package top.easelink.lcg.ui.search.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.easelink.framework.threadpool.IOPool
import top.easelink.lcg.ui.search.model.LCGSearchResultItem
import top.easelink.lcg.ui.search.source.LCGSearchService.doSearchNextPage
import top.easelink.lcg.ui.search.source.LCGSearchService.doSearchWith

class LCGSearchViewModel : ViewModel(), LCGSearchResultAdapter.ContentFetcher {
    val searchResults = MutableLiveData<List<LCGSearchResultItem>>()
    val totalResult = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()
    private var mKeyWord: String? = null

    fun setKeyword(keyword: String?) {
        mKeyWord = keyword
        fetch(LCGSearchResultAdapter.ContentFetcher.Type.INIT, null)
    }

    override fun fetch(
        type: LCGSearchResultAdapter.ContentFetcher.Type,
        callback: ((Boolean) -> Unit)?
    ) {
        if (type == LCGSearchResultAdapter.ContentFetcher.Type.INIT) {
            isLoading.value = true
        }
        mKeyWord?.let {
            GlobalScope.launch(IOPool) {
                when (type) {
                    LCGSearchResultAdapter.ContentFetcher.Type.INIT ->
                        doSearchWith(it).let {
                            if (!it.searchResultList.isNullOrEmpty()) {
                                searchResults.postValue(it.searchResultList)
                            }
                            if (!it.totalResult.isNullOrEmpty()) {
                                totalResult.postValue(it.totalResult)
                            }
                            callback?.invoke(!it.searchResultList.isNullOrEmpty())
                        }
                    LCGSearchResultAdapter.ContentFetcher.Type.NEXT_PAGE -> {
                        doSearchNextPage().let {
                            val list = searchResults.value
                            if (!it.searchResultList.isNullOrEmpty() && !list.isNullOrEmpty()) {
                                searchResults.postValue(list.plus(it.searchResultList))
                            }
                            totalResult.postValue(it.totalResult)
                            callback?.invoke(!it.searchResultList.isNullOrEmpty())
                        }
                    }
                }
                if (type == LCGSearchResultAdapter.ContentFetcher.Type.INIT) {
                    isLoading.postValue(false)
                }
            }
        }
    }
}