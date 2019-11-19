package top.easelink.lcg.ui.main.articles.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import top.easelink.framework.base.BaseViewModel;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.R;
import top.easelink.lcg.ui.main.articles.view.ArticlesNavigator;
import top.easelink.lcg.ui.main.source.local.ArticlesLocalDataSource;
import top.easelink.lcg.ui.main.source.model.ArticleEntity;

public class FavoriteArticlesViewModel extends BaseViewModel<ArticlesNavigator>
        implements FavoriteArticlesAdapter.ArticlesAdapterListener  {

    private final MutableLiveData<List<ArticleEntity>> articles = new MutableLiveData<>();
    private final ArticlesLocalDataSource articlesLocalDataSource = ArticlesLocalDataSource.getInstance();
    // TODO: 2019-07-27 add pagination for favorites 
    private int mCurrentPage = 0;

    public FavoriteArticlesViewModel(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }

    @Override
    public void fetchArticles(int type) {
        switch (type) {
            case FETCH_MORE:
                return;
            case FETCH_INIT:
            default:
                rewindPageNum();
                break;
        }
        setIsLoading(true);
        getCompositeDisposable().add(articlesLocalDataSource.getAllFavoriteArticles()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(articleEntityList -> {
                    if (articleEntityList != null && !articleEntityList.isEmpty()) {
                        articles.setValue(articleEntityList);
                    }
                }, throwable -> {
                    getNavigator().handleError(throwable);
                    setIsLoading(false);
                }, () -> setIsLoading(false)));
    }

    public void removeAllFavorites() {
        getCompositeDisposable().add(articlesLocalDataSource.delAllArticlesFromFavorite()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(
                        res -> {
                            getNavigator().showMessage(res?
                                    R.string.remove_all_favorites_successfully:
                                    R.string.remove_all_favorites_failed);
                            articles.setValue(new ArrayList<>(0));
                        },
                        throwable -> getNavigator().handleError(throwable)));
    }

    public LiveData<List<ArticleEntity>> getArticles() {
        return articles;
    }

    private void rewindPageNum() {
        mCurrentPage = 0;
    }

    private void nextPage() {
        mCurrentPage++;
    }
}
