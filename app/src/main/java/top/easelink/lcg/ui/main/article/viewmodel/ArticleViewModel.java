package top.easelink.lcg.ui.main.article.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import top.easelink.framework.base.BaseViewModel;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.ui.main.article.view.ArticleNavigator;
import top.easelink.lcg.ui.main.model.Post;
import top.easelink.lcg.ui.main.source.remote.RxArticleService;

import java.util.List;

public class ArticleViewModel extends BaseViewModel<ArticleNavigator> {

    private final MutableLiveData<List<Post>> posts = new MutableLiveData<>();
    private final RxArticleService articleService = RxArticleService.getInstance();

    public ArticleViewModel(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }

    public void fetchArticle(String url) {
        setIsLoading(true);
        getCompositeDisposable().add(articleService.getArticleDetail(url)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(postList -> {
                            if (postList != null && postList.size()!= 0) {
                                posts.setValue(postList);
                            }
                }, throwable -> getNavigator().handleError(throwable)
                        , () -> setIsLoading(false)));
    }

    public LiveData<List<Post>> getPosts() {
        return posts;
    }
}
