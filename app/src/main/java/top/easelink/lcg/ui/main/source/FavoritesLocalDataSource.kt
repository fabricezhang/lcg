package top.easelink.lcg.ui.main.source

import top.easelink.lcg.ui.main.source.model.ArticleEntity

/**
 * author : junzhang
 * date   : 2019-07-26 14:14
 * desc   :
 */
interface FavoritesLocalDataSource {
    fun getAllFavoriteArticles(): List<ArticleEntity>
    fun addArticleToFavorite(articleEntity: ArticleEntity): Boolean
    fun delArticleFromFavorite(id: String): Boolean
    fun delAllArticlesFromFavorite(): Boolean
}