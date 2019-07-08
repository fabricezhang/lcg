package top.easelink.lcg.ui.main.article.viewmodel;

import android.text.TextUtils;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import top.easelink.framework.base.BaseViewModel;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.ui.main.article.view.ArticleNavigator;
import top.easelink.lcg.ui.main.model.Post;
import top.easelink.lcg.ui.main.source.remote.RxArticleService;
import top.easelink.lcg.ui.main.model.BlockException;

import java.util.List;

public class ArticleViewModel extends BaseViewModel<ArticleNavigator> {

    private final MutableLiveData<List<Post>> mPosts = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsBlocked = new MutableLiveData<>();
    private final MutableLiveData<String> mArticleTitle = new MutableLiveData<>();
    private final RxArticleService articleService = RxArticleService.getInstance();

    public ArticleViewModel(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }

    public void fetchArticle(String url) {
        setIsLoading(true);
        getCompositeDisposable().add(articleService.getArticleDetail(url)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(articleDetail -> {
                    String title = articleDetail.getArticleTitle();
                    if (!TextUtils.isEmpty(title)) {
                        mArticleTitle.setValue(title);
                    }
                    List<Post> resPostList = articleDetail.getPostList();
                    if (resPostList != null && resPostList.size()!= 0) {
                        mPosts.setValue(resPostList);
                    }
                }, throwable -> {
                    if (throwable instanceof BlockException) {
                        mIsBlocked.setValue(true);
                        setIsLoading(false);
                    }
                    getNavigator().handleError(throwable);
                }, () -> setIsLoading(false)));
}

    public LiveData<List<Post>> getPosts() {
        return mPosts;
    }
    public LiveData<Boolean> getIsBlocked() {
        return mIsBlocked;
    }
    public LiveData<String> getArticleTitle() {
        return mArticleTitle;
    }
}
