package top.easelink.lcg.ui.main.source.local

import androidx.annotation.WorkerThread
import top.easelink.lcg.ui.main.source.FavoritesLocalDataSource
import top.easelink.lcg.ui.main.source.model.ArticleEntity

/**
 * author : junzhang
 * date   : 2019-07-26 14:15
 * desc   :
 */
object ArticlesLocalDataSource : FavoritesLocalDataSource {
    private val mArticlesDao = ArticlesDatabase.getInstance().articlesDao()

    @WorkerThread
    override fun getAllFavoriteArticles(): List<ArticleEntity> {
        return mArticlesDao.getArticles()
    }

    @WorkerThread
    override fun addArticleToFavorite(articleEntity: ArticleEntity): Boolean {
        mArticlesDao.insertArticle(articleEntity)
        return true
    }

    @WorkerThread
    override fun delArticleFromFavorite(id: String): Boolean {
        return mArticlesDao.deleteArticleById(id) == 1
    }

    @WorkerThread
    override fun delAllArticlesFromFavorite(): Boolean {
        mArticlesDao.deleteArticles()
        return true
    }
}