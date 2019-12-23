package top.easelink.lcg.ui.main.articles.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.source.model.Article
import top.easelink.lcg.ui.main.source.model.ForumThread
import top.easelink.lcg.ui.main.source.remote.ArticlesRemoteDataSource.getForumArticles
import top.easelink.lcg.utils.WebsiteConstant.FORUM_URL_TEMPLATE
import top.easelink.lcg.utils.showMessage

const val LAST_POST_ORDER = "&orderby=lastpost"
const val DATE_LINE_ORDER = "&orderby=dateline"
const val DEFAULT_ORDER = ""

class ForumArticlesViewModel : ViewModel(), ArticleFetcher {
    private var mUrl: String? = null
    private var mFetchType = ArticleFetcher.FetchType.FETCH_INIT
    private var orderType = DEFAULT_ORDER
    private var mCurrentPage = 1

    private var isTabSet = false

    val title = MutableLiveData<String>()
    val articles = MutableLiveData<List<Article>>()
    val threadList = MutableLiveData<List<ForumThread>>()
    val isLoading = MutableLiveData<Boolean>()

    fun initUrlAndFetch(url: String, fetchType: ArticleFetcher.FetchType, order: String = DEFAULT_ORDER) {
        //Fabrice: add a workaround to  map forum-16-1.html to forum.php?mod=forumdisplay&fid=16
        mUrl = if (url.startsWith("forum-") && url.endsWith("html")) {
            try {
                String.format(FORUM_URL_TEMPLATE, url.split("-")[1])
            } catch (e: Exception) {
                Timber.e(e)
                url
            }
        } else {
            url
        }
        mFetchType = fetchType
        orderType = order
        fetchArticles(mFetchType){}
    }

    @MainThread
    fun setTitle(t: String) {
        title.value = t
    }

    private fun composeUrlByRequestType(type: ArticleFetcher.FetchType): String {
        when (type) {
            ArticleFetcher.FetchType.FETCH_INIT -> rewindPageNum()
            ArticleFetcher.FetchType.FETCH_MORE -> nextPage()
        }
        return "$mUrl&page=$mCurrentPage$orderType"
    }

    override fun fetchArticles(fetchType: ArticleFetcher.FetchType, callback: (Boolean) -> Unit) {
        isLoading.value = true
        GlobalScope.launch(Dispatchers.IO) {
            val query = composeUrlByRequestType(fetchType)
            val forumPage = getForumArticles(query,
                fetchType == ArticleFetcher.FetchType.FETCH_INIT )
            if (forumPage != null) {
                val articleList = forumPage.articleList
                if (articleList.isNotEmpty()) {
                    val list = articles.value
                    if ((fetchType == ArticleFetcher.FetchType.FETCH_MORE) && !list.isNullOrEmpty()) {
                        val articleA = articleList[articleList.size - 1]
                        val articleB = list[list.size - 1]
                        if (articleA.title == articleB.title) {
                            showMessage(R.string.no_more_content)
                        } else {
                            articles.postValue(list.plus(articleList))
                        }
                    } else {
                        articles.postValue(articleList)
                    }
                }
                if (!isTabSet) {
                    forumPage.threadList.let {
                        threadList.postValue(
                            if (it.isNotEmpty())
                                it
                            else
                                emptyList()
                        )
                    }
                    isTabSet = true
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
}