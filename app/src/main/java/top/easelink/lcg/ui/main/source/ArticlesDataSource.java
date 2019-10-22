package top.easelink.lcg.ui.main.source;

import androidx.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;

import io.reactivex.Observable;
import top.easelink.lcg.ui.main.source.model.Article;
import top.easelink.lcg.ui.main.source.model.ArticleDetail;
import top.easelink.lcg.ui.main.source.model.ForumPage;

/**
 * author : junzhang
 * date   : 2019-07-26 14:59
 * desc   :
 */
public interface ArticlesDataSource {

    Observable<ForumPage> getForumArticles(@NonNull final String requestUrl, boolean processThreadList);

    Observable<ArticleDetail> getArticleDetail(@NonNull final String url);

    Observable<List<Article>> getHomePageArticles(@NonNull final String param, @Nullable final Integer pageNum);

}
