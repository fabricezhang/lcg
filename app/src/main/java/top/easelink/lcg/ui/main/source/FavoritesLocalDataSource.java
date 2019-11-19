package top.easelink.lcg.ui.main.source;

import java.util.List;

import io.reactivex.Observable;
import top.easelink.lcg.ui.main.source.model.ArticleEntity;

/**
 * author : junzhang
 * date   : 2019-07-26 14:14
 * desc   :
 */
public interface FavoritesLocalDataSource {

    Observable<List<ArticleEntity>> getAllFavoriteArticles();

    Observable<Boolean> addArticleToFavorite(ArticleEntity articleEntity);

    Observable<Boolean> delArticleFromFavorite(String id);

    Observable<Boolean> delAllArticlesFromFavorite();

}
