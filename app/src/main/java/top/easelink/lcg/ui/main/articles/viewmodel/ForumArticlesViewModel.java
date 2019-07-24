package top.easelink.lcg.ui.main.articles.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import top.easelink.framework.base.BaseViewModel;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.ui.main.articles.view.ArticlesNavigator;
import top.easelink.lcg.ui.main.model.Article;
import top.easelink.lcg.ui.main.source.remote.RxArticleService;

import java.util.List;

public class ForumArticlesViewModel extends BaseViewModel<ArticlesNavigator>
        implements ArticlesAdapter.ArticlesAdapterListener  {

    private String mUrl;
    private int mCurrentPage = 1;
    private MutableLiveData<String> mTitle = new MutableLiveData<>();

    private final MutableLiveData<List<Article>> articles = new MutableLiveData<>();
    private final RxArticleService articleService = RxArticleService.getInstance();
    private final MutableLiveData<Boolean> mShouldDisplayArticles = new MutableLiveData<>();

    public ForumArticlesViewModel(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }

    public void initUrl(String url) {
        mUrl = url;
        fetchArticles(FETCH_INIT);
    }

    public void setTitle(String title) {
        mTitle.setValue(title);
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
        getCompositeDisposable().add(articleService.getForumArticles(String.format(mUrl, mCurrentPage))
                .subscribeOn(getSchedulerProvider().ui())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(forumPage -> {
                    if (forumPage != null) {
                        List<Article> articleList = forumPage.getArticleList();
                        if (articleList != null && articleList.size()!= 0) {
                            List<Article> list = articles.getValue();
                            if (type == FETCH_MORE && list != null && list.size() != 0) {
                                list.addAll(articleList);
                                articles.setValue(list);
                            } else {
                                articles.setValue(articleList);
                            }
                        }
                        mShouldDisplayArticles.setValue(true);
                    }
                }, throwable -> {
                    getNavigator().handleError(throwable);
                }, () -> setIsLoading(false)));
    }

    public LiveData<List<Article>> getArticles() {
        return articles;
    }
    public LiveData<Boolean> getShouldDisplayArticles() {
        return mShouldDisplayArticles;
    }
    public LiveData<String> getTitle() {
        return mTitle;
    }

    private void rewindPageNum() {
        mCurrentPage = 1;
    }

    private void nextPage() {
        mCurrentPage++;
    }
}
