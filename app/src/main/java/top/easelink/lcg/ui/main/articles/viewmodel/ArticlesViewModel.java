package top.easelink.lcg.ui.main.articles.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import top.easelink.framework.base.BaseViewModel;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.ui.main.articles.view.ArticlesNavigator;
import top.easelink.lcg.ui.main.model.Article;
import top.easelink.lcg.ui.main.source.remote.RxArticleService;

import java.util.List;

public class ArticlesViewModel extends BaseViewModel<ArticlesNavigator> {

    public static final int FETCH_INIT = 0;
    public static final int FETCH_MORE = 1;
    private int mCurrentPage = 0;

    private final MutableLiveData<List<Article>> articles = new MutableLiveData<>();
    private final RxArticleService articleService = RxArticleService.getInstance();

    public ArticlesViewModel(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }

    public void fetchArticles(String param, int type) {
        int pageNum;
        switch (type) {
            case FETCH_MORE:
                pageNum = mCurrentPage+1;
                break;
            case FETCH_INIT:
            default:
                pageNum = 1;
                break;
        }
        setIsLoading(true);
        getCompositeDisposable().add(articleService.getArticles(param, pageNum)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(articleList -> {
                    if (articleList != null && articleList.size()!= 0) {
                        List<Article> list = articles.getValue();
                        if (type == FETCH_MORE && list != null && list.size() != 0) {
                            list.addAll(articleList);
                            articles.setValue(list);
                        } else {
                            articles.setValue(articleList);
                        }
                        // current page fetch successfully, record current page
                        mCurrentPage = pageNum;
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
