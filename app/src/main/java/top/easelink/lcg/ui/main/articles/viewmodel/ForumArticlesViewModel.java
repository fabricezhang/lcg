package top.easelink.lcg.ui.main.articles.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import top.easelink.framework.base.BaseViewModel;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.R;
import top.easelink.lcg.ui.main.articles.view.FourmArticleNavigator;
import top.easelink.lcg.ui.main.source.model.Article;
import top.easelink.lcg.ui.main.source.model.ForumThread;
import top.easelink.lcg.ui.main.source.remote.ArticlesRemoteDataSource;

public class ForumArticlesViewModel extends BaseViewModel<FourmArticleNavigator>
        implements ArticlesAdapter.ArticlesAdapterListener  {

    private String mUrl;
    private int mFetchType;
    private int mCurrentPage = 1;
    private MutableLiveData<String> mTitle = new MutableLiveData<>();

    private final MutableLiveData<List<Article>> articles = new MutableLiveData<>();
    private final MutableLiveData<List<ForumThread>> threads = new MutableLiveData<>();
    private final ArticlesRemoteDataSource articlesRemoteDataSource = ArticlesRemoteDataSource.getInstance();
    private final MutableLiveData<Boolean> mShouldDisplayArticles = new MutableLiveData<>();

    public ForumArticlesViewModel(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }

    public void initUrl(String url, int type) {
        mUrl = url;
        mFetchType = type;
        fetchArticles(mFetchType);
    }

    public void setTitle(String title) {
        mTitle.setValue(title);
    }

    @Override
    public void fetchArticles(int type) {
        setIsLoading(true);
        final String requestUrl;
        switch (type) {
            case FETCH_MORE:
                nextPage();
                // when scraping specified thread, use another URl format, see below
                // https://www.52pojie.cn/forum.php?mod=forumdisplay&fid=65&typeid=236&filter=typeid&typeid=236&page=2
                switch (mFetchType) {
                    case FETCH_BY_THREAD:
                        requestUrl = mUrl + String.format("&page=%s", mCurrentPage);
                        break;
                    case FETCH_INIT:
                    default:
                        requestUrl = String.format(mUrl, mCurrentPage);
                        break;
                }
                break;
            case FETCH_BY_THREAD:
                requestUrl = mUrl;
                rewindPageNum();
                break;
            case FETCH_INIT:
            default:
                requestUrl = String.format(mUrl, mCurrentPage);
                rewindPageNum();
                break;
        }

        getCompositeDisposable().add(
                articlesRemoteDataSource.getForumArticles(
                        requestUrl,
                        type == FETCH_INIT)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(forumPage -> {
                            if (forumPage != null) {
                                List<Article> articleList = forumPage.getArticleList();
                                if (articleList != null && articleList.size() > 0) {
                                    List<Article> list = articles.getValue();
                                    if (type == FETCH_MORE && list != null && list.size() > 0) {
                                        Article articleA = articleList.get(articleList.size()-1);
                                        Article articleB = list.get(list.size()-1);
                                        if (articleA.getTitle().equals(articleB.getTitle())){
                                            getNavigator().showMessage(R.string.no_more_content);
                                        } else {
                                            list.addAll(articleList);
                                            articles.setValue(list);
                                        }
                                    } else {
                                        articles.setValue(articleList);
                                    }
                                    mShouldDisplayArticles.setValue(true);
                                }
                                if (type == FETCH_INIT) {
                                    List<ForumThread> threadList = forumPage.getThreadList();
                                    if (threadList != null && threadList.size() > 0) {
                                        getNavigator().setUpTabLayout(threadList);
                                    }
                                }
                            }
                        }, throwable -> getNavigator().handleError(throwable), () -> setIsLoading(false)));
    }

    public LiveData<List<Article>> getArticles() {
        return articles;
    }

    public LiveData<List<ForumThread>> getForumThreads() {
        return threads;
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
