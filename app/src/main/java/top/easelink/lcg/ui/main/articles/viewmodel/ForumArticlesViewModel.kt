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

class ForumArticlesViewModel : ViewModel(),
    ArticleFetcher {
    private var mUrl: String? = null
    private var mFetchType = 0
    private var mCurrentPage = 1

    val title = MutableLiveData<String>()
    val articles = MutableLiveData<MutableList<Article>>()
    val shouldDisplayArticles = MutableLiveData<Boolean>()
    val threadList = MutableLiveData<List<ForumThread>>()
    val isLoading = MutableLiveData<Boolean>()

    fun initUrl(url: String, type: Int) {
        mUrl = url
        mFetchType = type
        fetchArticles(mFetchType)
    }

    @MainThread
    fun setTitle(t: String) {
        title.value = t
    }

    private fun composeUrlByRequestType(type: Int): String {
        return when (type) {
            ArticleFetcher.FETCH_MORE -> {
                nextPage()
                when (mFetchType) {
                    ArticleFetcher.FETCH_BY_THREAD -> mUrl + String.format(
                        "&page=%s",
                        mCurrentPage
                    )
                    ArticleFetcher.FETCH_INIT -> String.format(mUrl!!, mCurrentPage)
                    else -> String.format(mUrl!!, mCurrentPage)
                }
            }
            ArticleFetcher.FETCH_BY_THREAD -> {
                rewindPageNum()
                mUrl!!
            }
            ArticleFetcher.FETCH_INIT -> {
                rewindPageNum()
                String.format(mUrl!!, mCurrentPage)
            }
            else -> {
                rewindPageNum()
                String.format(mUrl!!, mCurrentPage)
            }
        }
    }

    override fun fetchArticles(type: Int) {
        isLoading.value = true
        GlobalScope.launch(Dispatchers.IO) {
            val query = composeUrlByRequestType(type)
            val forumPage = getForumArticles(query, type == ArticleFetcher.FETCH_INIT)
            if (forumPage != null) {
                val articleList = forumPage.articleList
                if (articleList.isNotEmpty()) {
                    val list = articles.value
                    if (type == ArticleFetcher.FETCH_MORE && list != null && list.size > 0) {
                        val articleA = articleList[articleList.size - 1]
                        val articleB = list[list.size - 1]
                        if (articleA.title == articleB.title) {
                            showMessage(R.string.no_more_content)
                        } else {
                            list.addAll(articleList)
                            articles.postValue(list)
                        }
                    } else {
                        articles.postValue(articleList)
                    }
                    shouldDisplayArticles.postValue(true)
                }
                if (type == ArticleFetcher.FETCH_INIT) {
                    val threads = forumPage.threadList
                    if (threads.size > 0) {
                        threadList.postValue(threads)
                    } else {
                        threadList.postValue(emptyList())
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
}