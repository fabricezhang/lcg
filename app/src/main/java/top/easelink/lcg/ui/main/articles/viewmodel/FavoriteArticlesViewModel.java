package top.easelink.lcg.ui.main.articles.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import top.easelink.framework.base.BaseViewModel;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.ui.main.articles.view.ArticlesNavigator;
import top.easelink.lcg.ui.main.source.ArticlesRepository;
import top.easelink.lcg.ui.main.source.model.ArticleEntity;

import java.util.List;

public class FavoriteArticlesViewModel extends BaseViewModel<ArticlesNavigator>
        implements FavoriteArticlesAdapter.ArticlesAdapterListener  {

    private final MutableLiveData<List<ArticleEntity>> articles = new MutableLiveData<>();
    private final ArticlesRepository articlesRepository = ArticlesRepository.getInstance();
    private int mCurrentPage = 0;

    public FavoriteArticlesViewModel(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }

    @Override
    public void fetchArticles(int type) {
        setIsLoading(true);
        switch (type) {
            case FETCH_MORE:
                nextPage();
                break;
            case FETCH_INIT:
            default:
                rewindPageNum();
                break;
        }
        getCompositeDisposable().add(articlesRepository.getAllFavoriteArticles()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(articleEntityList -> {
                    if (articleEntityList != null && !articleEntityList.isEmpty()) {
                        List<ArticleEntity> list = articles.getValue();
                        if (type == FETCH_MORE && list != null && list.size() != 0) {
                            list.addAll(articleEntityList);
                            articles.setValue(list);
                        } else {
                            articles.setValue(articleEntityList);
                        }
                    }
                }, throwable -> {
                    getNavigator().handleError(throwable);
                }, () -> setIsLoading(false)));
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
