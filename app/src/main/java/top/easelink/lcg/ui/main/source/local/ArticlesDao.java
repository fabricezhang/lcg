package top.easelink.lcg.ui.main.source.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import top.easelink.lcg.ui.main.source.model.ArticleEntity;

import java.util.List;

/**
 * author : junzhang
 * date   : 2019-07-26 14:02
 * desc   : Data Access Object for the articles table.
 */

@Dao
public interface ArticlesDao {

    /**
     * Insert an article in the database.
     * If the article already exists, replace it.
     *
     * @param articleEntity the article to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticle(ArticleEntity articleEntity);

    /**
     * Select all articles from my collections.
     *
     * @return all collected articles.
     */
    @Query("SELECT * FROM articles")
    List<ArticleEntity> getArticles();

    /**
     * Delete an article by id.
     *
     * @return the number of articles deleted. This should always be 1.
     */
    @Query("DELETE FROM articles WHERE id = :id")
    int deleteArticleById(String id);

    /**
     * Delete all articles.
     */
    @Query("DELETE FROM articles")
    void deleteArticles();
}
