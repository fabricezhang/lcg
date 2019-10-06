package top.easelink.lcg.ui.main.article.viewmodel;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jsoup.HttpStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import top.easelink.framework.base.BaseViewModel;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.R;
import top.easelink.lcg.ui.main.article.view.ArticleNavigator;
import top.easelink.lcg.ui.main.model.BlockException;
import top.easelink.lcg.ui.main.source.ArticlesRepository;
import top.easelink.lcg.ui.main.source.model.ArticleAbstractResponse;
import top.easelink.lcg.ui.main.source.model.ArticleEntity;
import top.easelink.lcg.ui.main.source.model.Post;
import top.easelink.lcg.utils.RegexUtils;

public class ArticleViewModel extends BaseViewModel<ArticleNavigator>
        implements ArticleAdapterListener {

    public static final int FETCH_INIT = 0;
    public static final int FETCH_MORE = 1;

    private final MutableLiveData<List<Post>> mPosts = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsBlocked = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsNotFound = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mShouldDisplayPosts = new MutableLiveData<>();
    private final MutableLiveData<String> mArticleTitle = new MutableLiveData<>();
    private final ArticlesRepository articlesRepository = ArticlesRepository.getInstance();
    private String mUrl;
    private String nextPageUrl;
    private ArticleAbstractResponse articleAbstract;

    public ArticleViewModel(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    @Override
    public void fetchArticlePost(int type) {
        setIsLoading(true);
        String requestUrl;
        if (type == FETCH_INIT) {
            requestUrl = mUrl;
        } else {
            if (TextUtils.isEmpty(nextPageUrl)) {
                // no more content
                setIsLoading(false);
                return;
            } else {
                requestUrl = nextPageUrl;
            }
        }
        getCompositeDisposable().add(articlesRepository.getArticleDetail(requestUrl)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(articleDetail -> {
                    articleAbstract = articleDetail.getArticleAbstractResponse();
                    String title = articleDetail.getArticleTitle();
                    if (!TextUtils.isEmpty(title)) {
                        mArticleTitle.setValue(title);
                    }
                    nextPageUrl = articleDetail.getNextPageUrl();
                    List<Post> resPostList = articleDetail.getPostList();
                    if (resPostList != null && resPostList.size()!= 0) {
                        if (type == FETCH_INIT) {
                            mPosts.setValue(resPostList);
                        } else {
                            List<Post> list = mPosts.getValue();
                            if (list != null && !list.isEmpty()) {
                                list.addAll(resPostList);
                                mPosts.setValue(list);
                            } else {
                                mPosts.setValue(resPostList);
                            }
                        }
                    }
                    mShouldDisplayPosts.setValue(true);
                }, throwable -> {
                    if (throwable instanceof BlockException) {
                        setArticleBlocked();
                    } else if (throwable instanceof HttpStatusException) {
                        setArticleNotFound();
                    }
                    setIsLoading(false);
                    getNavigator().handleError(throwable);
                }, () -> setIsLoading(false)));
    }

    @Nullable
    public ArrayList<String> extractDownloadUrl() {
        String patternLanzous = "https://www.lanzous.com/[a-zA-Z0-9]{4,10}";
        String patternBaidu = "https://pan.baidu.com/s/.{23}";
        String patternT = "http://t.cn/[a-zA-Z0-9]{8}";
        List<Post> list = mPosts.getValue();
        HashSet<String> resSet = null;
        if (list != null && !list.isEmpty()) {
            String content = list.get(0).getContent();
            resSet = RegexUtils.extractInfoFrom(content, patternLanzous);
            resSet.addAll(RegexUtils.extractInfoFrom(content, patternBaidu));
            resSet.addAll(RegexUtils.extractInfoFrom(content, patternT));
        }
        return resSet != null? new ArrayList<>(resSet):null;
    }

    public void addToFavorite() {
        List<Post> posts = getPosts().getValue();
        if (posts == null || posts.isEmpty()) {
            getNavigator().showMessage(R.string.add_to_favorite_failed);
            return;
        }
        String title = mArticleTitle.getValue();
        if (title == null) {
            // if title is null, use abstract's title, this rarely happens
            title = articleAbstract.title;
        }
        String author = posts.get(0).getAuthor();
        String content = articleAbstract==null?"":articleAbstract.description;
        ArticleEntity articleEntity = new ArticleEntity(title == null?"未知标题":title, author, mUrl, content, System.currentTimeMillis());
        getCompositeDisposable().add(articlesRepository.addArticleToFavorite(articleEntity)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(
                        res -> getNavigator().showMessage(res?
                                R.string.add_to_favorite_successfully:
                                R.string.add_to_favorite_failed),
                        throwable -> getNavigator().handleError(throwable)));
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
    public LiveData<Boolean> getIsNotFound() {
        return mIsNotFound;
    }
    public LiveData<Boolean> getShouldDisplayPosts() {
        return mShouldDisplayPosts;
    }

    public void scrollToTop() {
        getNavigator().scrollToTop();
    }

    private void setArticleNotFound() {
        mIsNotFound.setValue(true);
        mShouldDisplayPosts.setValue(false);
    }

    private void setArticleBlocked() {
        mIsBlocked.setValue(true);
        mShouldDisplayPosts.setValue(false);
    }
}
