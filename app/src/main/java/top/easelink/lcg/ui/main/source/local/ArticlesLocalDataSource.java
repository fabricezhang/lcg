package top.easelink.lcg.ui.main.source.local;

import java.util.List;

import io.reactivex.Observable;
import top.easelink.lcg.ui.main.source.FavoritesDataSource;
import top.easelink.lcg.ui.main.source.model.ArticleEntity;

/**
 * author : junzhang
 * date   : 2019-07-26 14:15
 * desc   :
 */
public class ArticlesLocalDataSource implements FavoritesDataSource {

    private static volatile ArticlesLocalDataSource mInstance;

    private ArticlesDao mArticlesDao = ArticlesDatabase.getInstance().articlesDao();

    public static ArticlesLocalDataSource getInstance() {
        if (mInstance == null) {
            synchronized (ArticlesLocalDataSource.class) {
                if (mInstance == null) {
                    mInstance = new ArticlesLocalDataSource();
                }
            }
        }
        return mInstance;
    }

    @Override
    public Observable<List<ArticleEntity>> getAllFavoriteArticles() {
        return Observable.fromCallable(mArticlesDao::getArticles);
    }

    @Override
    public Observable<Boolean> addArticleToFavorite(ArticleEntity articleEntity) {
        return Observable.fromCallable(()->{
            mArticlesDao.insertArticle(articleEntity);
            return true;
        });
    }

    @Override
    public Observable<Boolean> delArticleFromFavorite(String id) {
        return Observable.fromCallable(()->{
            return mArticlesDao.deleteArticleById(id) == 1;
        });
    }

    @Override
    public Observable<Boolean> delAllArticlesFromFavorite() {
        return Observable.fromCallable(()->{
            mArticlesDao.deleteArticles();
            return true;
        });
    }
}
