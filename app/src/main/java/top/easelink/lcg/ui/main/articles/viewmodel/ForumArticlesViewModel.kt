package top.easelink.lcg.ui.main.articles.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.source.model.Article
import top.easelink.lcg.ui.main.source.model.ForumThread
import top.easelink.lcg.ui.main.source.remote.ArticlesRemoteDataSource.getForumArticles
import top.easelink.lcg.utils.showMessage

const val LAST_POST_ORDER = "&orderby=lastpost"
const val DATE_LINE_ORDER = "&orderby=dateline"
const val DEFAULT_ORDER = ""

class ForumArticlesViewModel : ViewModel(), ArticleFetcher {
    private var mUrl: String? = null
    private var mFetchType = ArticleFetcher.FetchType.FETCH_INIT
    private var mPageType = PageType.DEFAULT_PAGE
    private var orderType = DEFAULT_ORDER
    private var mCurrentPage = 1

    val title = MutableLiveData<String>()
    val articles = MutableLiveData<List<Article>>()
    val shouldDisplayArticles = MutableLiveData<Boolean>()
    val threadList = MutableLiveData<List<ForumThread>>()
    val isLoading = MutableLiveData<Boolean>()

    fun initUrlAndFetch(url: String, fetchType: ArticleFetcher.FetchType, pageType: PageType, order: String = DEFAULT_ORDER) {
        mUrl = url
        mFetchType = fetchType
        mPageType = pageType
        orderType = order
        fetchArticles(mFetchType)
    }

    @MainThread
    fun setTitle(t: String) {
        title.value = t
    }

    private fun composeUrlByRequestType(type: ArticleFetcher.FetchType, pageType: PageType): String {
        when (type) {
            ArticleFetcher.FetchType.FETCH_INIT -> rewindPageNum()
            ArticleFetcher.FetchType.FETCH_MORE -> nextPage()
        }
        return when (pageType) {
            PageType.DEFAULT_PAGE -> {
                String.format(mUrl!!, mCurrentPage)
            }
            PageType.THREAD_PAGE -> {
                "$mUrl&page=$mCurrentPage$orderType"
            }
        }
    }

    override fun fetchArticles(fetchType: ArticleFetcher.FetchType) {
        isLoading.value = true
        GlobalScope.launch(Dispatchers.IO) {
            val query = composeUrlByRequestType(fetchType, mPageType)
            val forumPage = getForumArticles(query, mPageType == PageType.DEFAULT_PAGE)
            if (forumPage != null) {
                val articleList = forumPage.articleList
                if (articleList.isNotEmpty()) {
                    val list = articles.value
                    if ((fetchType == ArticleFetcher.FetchType.FETCH_MORE)
                        && !list.isNullOrEmpty()) {
                        val articleA = articleList[articleList.size - 1]
                        val articleB = list[list.size - 1]
                        if (articleA.title == articleB.title) {
                            showMessage(R.string.no_more_content)
                        } else {
                            list.toMutableList().addAll(articleList)
                            articles.postValue(list)
                        }
                    } else {
                        articles.postValue(articleList)
                    }
                    shouldDisplayArticles.postValue(true)
                }
                if (mPageType == PageType.DEFAULT_PAGE) {
                    forumPage.threadList.let {
                        threadList.postValue(
                            if (it.isNotEmpty())
                                it
                            else
                                emptyList()
                        )
                    }
                }

            }
            isLoading.postValue(false)
        }
    }

    private fun rewindPageNum() {
        mCurrentPage = 1
    }

    private fun nextPage() {
        mCurrentPage++
    }

    enum class PageType{
        DEFAULT_PAGE,
        THREAD_PAGE
    }
}