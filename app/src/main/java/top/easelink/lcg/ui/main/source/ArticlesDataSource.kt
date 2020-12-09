package top.easelink.lcg.ui.main.source

import androidx.annotation.WorkerThread
import top.easelink.lcg.ui.main.source.model.Article
import top.easelink.lcg.ui.main.source.model.ArticleDetail
import top.easelink.lcg.ui.main.source.model.ForumPage
import top.easelink.lcg.ui.main.source.model.PreviewPost

/**
 * author : junzhang
 * date   : 2019-07-26 14:59
 * desc   :
 */
interface ArticlesDataSource {
    @WorkerThread
    fun getForumArticles(query: String, processThreadList: Boolean): ForumPage?

    @WorkerThread
    fun getArticleDetail(query: String, isFirstFetch: Boolean): ArticleDetail?

    @WorkerThread
    fun getPostPreview(query: String): PreviewPost?

    @WorkerThread
    fun getHomePageArticles(param: String, pageNum: Int): List<Article>
}