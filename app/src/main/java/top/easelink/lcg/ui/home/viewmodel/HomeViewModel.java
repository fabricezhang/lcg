package top.easelink.lcg.ui.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import top.easelink.framework.base.BaseViewModel;
import top.easelink.lcg.ui.home.model.Article;
import top.easelink.lcg.ui.home.source.remote.RxArticleService;
import top.easelink.lcg.ui.home.view.HomeNavigator;

import java.util.List;

public class HomeViewModel extends BaseViewModel<HomeNavigator> {

    private final MutableLiveData<List<Article>> articles = new MutableLiveData<>();
    private final RxArticleService articleService = RxArticleService.getInstance();

    public HomeViewModel() {

    }

    public void onNavBackClick() {
        getNavigator().goBack();
    }

    public void fetchArticles() {
        setIsLoading(true);
        getCompositeDisposable().add(articleService.getArticles(1)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(articleList -> {
                    if (articleList != null && articleList.size()!= 0) {
                        articles.setValue(articleList);
                    }
                    setIsLoading(false);
                }, throwable -> {
                    setIsLoading(false);
                    getNavigator().handleError(throwable);
                }));
    }

    public LiveData<List<Article>> getArticles() {
        return articles;
    }
}
