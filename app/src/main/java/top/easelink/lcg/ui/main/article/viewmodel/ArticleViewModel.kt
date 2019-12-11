package top.easelink.lcg.ui.main.article.viewmodel

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.HttpStatusException
import timber.log.Timber
import top.easelink.framework.base.BaseViewModel
import top.easelink.framework.utils.rx.SchedulerProvider
import top.easelink.lcg.R
import top.easelink.lcg.mta.EVENT_ADD_TO_FAVORITE
import top.easelink.lcg.mta.sendEvent
import top.easelink.lcg.ui.main.article.view.ArticleNavigator
import top.easelink.lcg.ui.main.model.BlockException
import top.easelink.lcg.ui.main.source.local.ArticlesLocalDataSource
import top.easelink.lcg.ui.main.source.local.SPConstants
import top.easelink.lcg.ui.main.source.local.SharedPreferencesHelper
import top.easelink.lcg.ui.main.source.model.ArticleAbstractResponse
import top.easelink.lcg.ui.main.source.model.ArticleDetail
import top.easelink.lcg.ui.main.source.model.ArticleEntity
import top.easelink.lcg.ui.main.source.model.Post
import top.easelink.lcg.ui.main.source.remote.ArticlesRemoteDataSource
import top.easelink.lcg.utils.RegexUtils
import top.easelink.lcg.utils.showMessage
import java.util.*

class ArticleViewModel(schedulerProvider: SchedulerProvider?) :
    BaseViewModel<ArticleNavigator?>(schedulerProvider), ArticleAdapterListener {
    private val mPosts =
        MutableLiveData<MutableList<Post>>()
    private val mIsBlocked = MutableLiveData<Boolean>()
    private val mIsNotFound = MutableLiveData<Boolean>()
    private val mShouldDisplayPosts = MutableLiveData<Boolean>()
    private val mArticleTitle = MutableLiveData<String>()

    private var mUrl: String? = null
    private var nextPageUrl: String? = null
    // formhash is used for add favorite/reply/rate etc
    private var mFormHash: String? = null
    private var articleAbstract: ArticleAbstractResponse? = null

    fun setUrl(url: String?) {
        mUrl = url
    }

    override fun fetchArticlePost(type: Int) {
        setIsLoading(true)
        val requestUrl: String? =
            if (type == FETCH_INIT) {
                mUrl
            } else if (TextUtils.isEmpty(nextPageUrl)) { // no more content
                setIsLoading(false)
                return
            } else {
                nextPageUrl
            }

        compositeDisposable.add(ArticlesRemoteDataSource.getArticleDetail(requestUrl!!)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe(
                { articleDetail: ArticleDetail ->
                    articleAbstract = articleDetail.articleAbstractResponse
                    val title = articleDetail.articleTitle
                    if (!TextUtils.isEmpty(title)) {
                        mArticleTitle.value = title
                    }
                    nextPageUrl = articleDetail.nextPageUrl
                    val resPostList =
                        articleDetail.postList
                    if (resPostList != null && resPostList.size != 0) {
                        if (type == FETCH_INIT) {
                            mPosts.setValue(resPostList)
                        } else {
                            val list = mPosts.value
                            if (list != null && !list.isEmpty()) {
                                list.addAll(resPostList)
                                mPosts.setValue(list)
                            } else {
                                mPosts.setValue(resPostList)
                            }
                        }
                    }
                    mFormHash = articleDetail.fromHash
                    mShouldDisplayPosts.setValue(true)
                },
                { throwable: Throwable? ->
                    if (throwable is BlockException) {
                        setArticleBlocked()
                    } else if (throwable is HttpStatusException) {
                        setArticleNotFound()
                    }
                    setIsLoading(false)
                    navigator!!.handleError(throwable)
                }
            ) { setIsLoading(false) }
        )
    }

    override fun replyAdd(url: String) {
        if (TextUtils.isEmpty(url)) {
            setIsLoading(false)
            throw IllegalStateException()
        }
        compositeDisposable.add(ArticlesRemoteDataSource.replyAdd(url)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe({ msg -> showMessage(msg) },
                { throwable: Throwable -> }
            ) { setIsLoading(false) }
        )
    }

    fun extractDownloadUrl(): ArrayList<String>? {
        val patternLanzous = "https://www.lanzous.com/[a-zA-Z0-9]{4,10}"
        val patternBaidu = "https://pan.baidu.com/s/.{23}"
        val patternT = "http://t.cn/[a-zA-Z0-9]{8}"
        val list: List<Post>? = mPosts.value
        var resSet: HashSet<String>? = null
        if (list != null && !list.isEmpty()) {
            val content = list[0].content
            resSet = RegexUtils.extractInfoFrom(content, patternLanzous)
            resSet.addAll(RegexUtils.extractInfoFrom(content, patternBaidu))
            resSet.addAll(RegexUtils.extractInfoFrom(content, patternT))
        }
        return resSet?.let {
            ArrayList(it)
        }
    }

    fun addToFavorite() {
        sendEvent(EVENT_ADD_TO_FAVORITE)
        val posts: MutableList<Post> = posts.value?: mutableListOf()
        if (posts.isEmpty()) {
            navigator!!.showMessage(R.string.add_to_favorite_failed)
            return
        }
        var title = mArticleTitle.value
        if (title == null) { // if title is null, use abstract's title, this rarely happens
            title = articleAbstract!!.title
        }
        val threadId = extractThreadId(mUrl)
        val author = posts[0].author
        val content =
            if (articleAbstract == null) "" else articleAbstract!!.description
        val articleEntity = ArticleEntity(
            title ?: "未知标题",
            author,
            mUrl!!,
            content,
            System.currentTimeMillis()
        )
        val syncFavoritesEnable =
            SharedPreferencesHelper.getUserSp().getBoolean(SPConstants.SP_KEY_SYNC_FAVORITE, false)
        if (syncFavoritesEnable) {
            if (threadId != null && mFormHash != null) {
                compositeDisposable.add(
                    ArticlesRemoteDataSource.addFavorites(threadId, mFormHash!!)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui()).subscribe(
                            { res: Boolean ->
                                navigator!!.showMessage(
                                    if (res) R.string.sync_favorite_successfully else R.string.sync_favorite_failed
                                )
                            }
                        ) { throwable: Throwable? ->
                            navigator!!.handleError(
                                throwable
                            )
                        }
                )
            }
        }
        GlobalScope.launch(Dispatchers.IO){
            try {
                if (ArticlesLocalDataSource.addArticleToFavorite(articleEntity)) {
                    showMessage(R.string.add_to_favorite_successfully)
                } else {
                    showMessage(R.string.add_to_favorite_failed)
                }
            } catch (e: Exception) {
                Timber.e(e)
                showMessage(R.string.add_to_favorite_failed)
            }
        }
    }

    val posts: LiveData<MutableList<Post>>
        get() = mPosts

    val isBlocked: LiveData<Boolean>
        get() = mIsBlocked

    val articleTitle: LiveData<String>
        get() = mArticleTitle

    val isNotFound: LiveData<Boolean>
        get() = mIsNotFound

    val shouldDisplayPosts: LiveData<Boolean>
        get() = mShouldDisplayPosts

    fun scrollToTop() {
        navigator!!.scrollToTop()
    }

    private fun setArticleNotFound() {
        mIsNotFound.value = true
        mShouldDisplayPosts.value = false
    }

    private fun setArticleBlocked() {
        mIsBlocked.value = true
        mShouldDisplayPosts.value = false
    }

    private fun extractThreadId(url: String?): String? {
        return if (TextUtils.isEmpty(url)) {
            null
        } else try {
            url!!.split("-").toTypedArray()[1]
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    companion object {
        const val FETCH_INIT = 0
        const val FETCH_MORE = 1
    }
}