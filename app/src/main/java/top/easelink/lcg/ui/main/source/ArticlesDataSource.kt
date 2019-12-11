package top.easelink.lcg.ui.main.source

import androidx.annotation.WorkerThread
import io.reactivex.Observable
import top.easelink.lcg.ui.main.source.model.Article
import top.easelink.lcg.ui.main.source.model.ArticleDetail
import top.easelink.lcg.ui.main.source.model.ForumPage

/**
 * author : junzhang
 * date   : 2019-07-26 14:59
 * desc   :
 */
interface ArticlesDataSource {
    @WorkerThread
    fun getForumArticles(query: String, processThreadList: Boolean): ForumPage?

    fun getArticleDetail(url: String): Observable<ArticleDetail>

    @WorkerThread
    fun getHomePageArticles(param: String, pageNum: Int): List<Article>
}