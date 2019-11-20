package top.easelink.lcg.ui.main.article.viewmodel;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jsoup.HttpStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import timber.log.Timber;
import top.easelink.framework.base.BaseViewModel;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.R;
import top.easelink.lcg.mta.EventHelperKt;
import top.easelink.lcg.ui.main.article.view.ArticleNavigator;
import top.easelink.lcg.ui.main.model.BlockException;
import top.easelink.lcg.ui.main.source.local.ArticlesLocalDataSource;
import top.easelink.lcg.ui.main.source.local.SharedPreferencesHelper;
import top.easelink.lcg.ui.main.source.model.ArticleAbstractResponse;
import top.easelink.lcg.ui.main.source.model.ArticleEntity;
import top.easelink.lcg.ui.main.source.model.Post;
import top.easelink.lcg.ui.main.source.remote.ArticlesRemoteDataSource;
import top.easelink.lcg.utils.RegexUtils;

import static top.easelink.lcg.mta.MTAConstantKt.EVENT_ADD_TO_FAVORITE;
import static top.easelink.lcg.ui.main.source.local.SPConstants.SP_KEY_SYNC_FAVORITE;

public class ArticleViewModel extends BaseViewModel<ArticleNavigator>
        implements ArticleAdapterListener {

    public static final int FETCH_INIT = 0;
    public static final int FETCH_MORE = 1;

    private final MutableLiveData<List<Post>> mPosts = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsBlocked = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsNotFound = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mShouldDisplayPosts = new MutableLiveData<>();
    private final MutableLiveData<String> mArticleTitle = new MutableLiveData<>();
    private final ArticlesRemoteDataSource articlesRemoteDataSource = ArticlesRemoteDataSource.getInstance();
    private final ArticlesLocalDataSource articlesLocalDataSource = ArticlesLocalDataSource.getInstance();
    private String mUrl;
    private String nextPageUrl;
    // formhash is used for add favorite/reply/rate etc
    private String mFormHash = null;
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
        getCompositeDisposable().add(articlesRemoteDataSource.getArticleDetail(requestUrl)
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
                    mFormHash = articleDetail.getFromHash();
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
        EventHelperKt.sendEvent(EVENT_ADD_TO_FAVORITE);
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
        String threadId = extractThreadId(mUrl);
        String author = posts.get(0).getAuthor();
        String content = articleAbstract==null?"":articleAbstract.description;
        ArticleEntity articleEntity = new ArticleEntity(title == null?"未知标题":title, author, mUrl, content, System.currentTimeMillis());
        boolean syncFavoritesEnable = SharedPreferencesHelper.getUserSp().getBoolean(SP_KEY_SYNC_FAVORITE, false);
        if (syncFavoritesEnable) {
            if (threadId != null && mFormHash != null) {
                getCompositeDisposable().add(articlesRemoteDataSource.addFavorites(threadId, mFormHash)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui()).subscribe(
                                res -> getNavigator().showMessage(res ?
                                        R.string.sync_favorite_successfully :
                                        R.string.sync_favorite_failed),
                                throwable -> getNavigator().handleError(throwable)
                        ));
            }
        }
        getCompositeDisposable().add(articlesLocalDataSource.addArticleToFavorite(articleEntity)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(
                        res -> {
                            getNavigator().showMessage(res ?
                                    R.string.add_to_favorite_successfully :
                                    R.string.add_to_favorite_failed);
                        },
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

    private String extractThreadId(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        try {
            return url.split("-")[1];
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }
}
