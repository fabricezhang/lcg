package top.easelink.lcg.ui.main.source;

import io.reactivex.Observable;
import top.easelink.lcg.ui.main.source.model.ArticleEntity;

import java.util.List;

/**
 * author : junzhang
 * date   : 2019-07-26 14:14
 * desc   :
 */
public interface FavoritesDataSource {

    Observable<List<ArticleEntity>> getAllFavoriteArticles();

    Observable<Boolean> addArticleToFavorite(ArticleEntity articleEntity);

    Observable<Boolean> delArticleFromFavorite(String id);

    Observable<Boolean> delAllArticlesFromFavorite();

}
