package top.easelink.lcg.ui.main.source;

import androidx.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;

import io.reactivex.Observable;
import top.easelink.lcg.ui.main.source.local.ArticlesLocalDataSource;
import top.easelink.lcg.ui.main.source.model.Article;
import top.easelink.lcg.ui.main.source.model.ArticleDetail;
import top.easelink.lcg.ui.main.source.model.ArticleEntity;
import top.easelink.lcg.ui.main.source.model.ForumPage;
import top.easelink.lcg.ui.main.source.remote.ArticlesRemoteDataSource;

/**
 * author : junzhang
 * date   : 2019-07-26 14:13
 * desc   :
 */
public class ArticlesRepository implements FavoritesDataSource, ArticlesDataSource {

    private final ArticlesLocalDataSource localDataSource;
    private final ArticlesRemoteDataSource remoteDataSource;
    private volatile static ArticlesRepository mInstance = null;

    private ArticlesRepository() {
        localDataSource = ArticlesLocalDataSource.getInstance();
        remoteDataSource = ArticlesRemoteDataSource.getInstance();
    }

    public static ArticlesRepository getInstance() {
        if (mInstance == null) {
            synchronized (ArticlesRepository.class) {
                if (mInstance == null) {
                    mInstance = new ArticlesRepository();
                }
            }
        }
        return mInstance;
    }

    @Override
    public Observable<List<ArticleEntity>> getAllFavoriteArticles() {
        return localDataSource.getAllFavoriteArticles();
    }

    @Override
    public Observable<Boolean> addArticleToFavorite(ArticleEntity articleEntity) {
        return localDataSource.addArticleToFavorite(articleEntity);
    }

    @Override
    public Observable<Boolean> delArticleFromFavorite(String id) {
        return localDataSource.delArticleFromFavorite(id);
    }

    @Override
    public Observable<Boolean> delAllArticlesFromFavorite() {
        return localDataSource.delAllArticlesFromFavorite();
    }

    @Override
    public Observable<ForumPage> getForumArticles(@NonNull String requestUrl, boolean processThreadList) {
        return remoteDataSource.getForumArticles(requestUrl, processThreadList);
    }

    @Override
    public Observable<ArticleDetail> getArticleDetail(@NonNull String url) {
        return remoteDataSource.getArticleDetail(url);
    }

    @Override
    public Observable<List<Article>> getHomePageArticles(@NonNull String param, @Nullable Integer pageNum) {
        return remoteDataSource.getHomePageArticles(param, pageNum);
    }
}
