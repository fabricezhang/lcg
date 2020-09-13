package top.easelink.lcg.ui.main.source.local

import androidx.room.*
import top.easelink.lcg.ui.main.source.model.ArticleEntity
import top.easelink.lcg.ui.main.source.model.HistoryEntity

/**
 * author : junzhang
 * date   : 2019-07-26 14:02
 * desc   : Data Access Object for the articles table.
 */
@Dao
interface ArticlesDao {

    // region Articles Management
    /**
     * Insert an article in the database.
     * If the article already exists, replace it.
     *
     * @param articleEntity the article to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(articleEntity: ArticleEntity)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllArticles(articleEntities: List<ArticleEntity>)

    /**
     * Select all articles from my collections.
     *
     * @return all collected articles.
     */
    @Query("SELECT * FROM articles")
    fun getArticles(): List<ArticleEntity>

    /**
     * Delete an article by id.
     *
     * @return the number of articles deleted. This should always be 1.
     */
    @Query("DELETE FROM articles WHERE id = :id")
    fun deleteArticleById(id: String): Int

    /**
     * Delete all articles.
     */
    @Query("DELETE FROM articles")
    fun deleteArticles()

    //endregion

    //region History Management
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    suspend fun getHistories(): List<HistoryEntity>

    @Insert
    suspend fun insertHistory(vararg historyEntity: HistoryEntity)

    @Delete
    suspend fun deleteHistory(vararg historyEntity: HistoryEntity)

    @Query("DELETE FROM history")
    suspend fun deleteHistories()

    //endregion

}