package top.easelink.lcg.ui.main.articles.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.easelink.lcg.ui.main.source.model.Article
import top.easelink.lcg.ui.main.source.remote.ArticlesRemoteDataSource

class ArticlesViewModel : ViewModel(), ArticleFetcher {
    private var mCurrentPage = 0
    private var mUrl: String? = null
    val isLoading = MutableLiveData<Boolean>()
    val articles = MutableLiveData<List<Article>>()

    fun initUrl(url: String?) {
        mUrl = url
        fetchArticles(ArticleFetcher.FetchType.FETCH_INIT){}
    }

    override fun fetchArticles(fetchType: ArticleFetcher.FetchType, callback: (Boolean) -> Unit) {
        val pageNum: Int = when (fetchType) {
            ArticleFetcher.FetchType.FETCH_MORE -> mCurrentPage + 1
            ArticleFetcher.FetchType.FETCH_INIT -> 1
        }
        isLoading.value = true
        GlobalScope.launch(Dispatchers.IO){
            ArticlesRemoteDataSource.getHomePageArticles(mUrl!!, pageNum).let {
                if (it.isNotEmpty()) {
                    val list = articles.value?.toMutableList()
                    if (fetchType == ArticleFetcher.FetchType.FETCH_MORE && list != null && list.size != 0) {
                        list.addAll(it)
                        articles.postValue(list)
                    } else {
                        articles.postValue(it)
                    }
                    // current page fetch successfully, record current page
                    mCurrentPage = pageNum
                    callback.invoke(true)
                } else {
                    callback.invoke(false)
                }
            }
            isLoading.postValue(false)
        }
    }
}