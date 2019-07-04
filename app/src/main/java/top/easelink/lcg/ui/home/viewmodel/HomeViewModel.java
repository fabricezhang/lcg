package top.easelink.lcg.ui.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import top.easelink.framework.base.BaseViewModel;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.ui.home.model.Article;
import top.easelink.lcg.ui.home.view.HomeNavigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeViewModel extends BaseViewModel<HomeNavigator> {

    private final MutableLiveData<Boolean> mDataLoading = new MutableLiveData<>();
    private final MutableLiveData<List<Article>> articles = new MutableLiveData<>();

    public HomeViewModel(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
        fetchArticles();
    }

    public void onNavBackClick() {
        getNavigator().goBack();
    }

    public void fetchArticles() {
        mDataLoading.setValue(true);
        List<Article> articleList = new ArrayList<>();
        Random random = new Random();
        articleList.add(new Article("Article" + random.nextInt(),  "0"));
        articleList.add(new Article("Article" + random.nextInt(),  "1"));
        articleList.add(new Article("Article" + random.nextInt(),  "2"));
        articleList.add(new Article("Article" + random.nextInt(),  "3"));
        articleList.add(new Article("Article" + random.nextInt(),  "4"));
        articleList.add(new Article("Article" + random.nextInt(),  "5"));
        articles.setValue(articleList);
        mDataLoading.setValue(false);
    }

    public LiveData<Boolean> isDataLoading() {
        return mDataLoading;
    }
    public LiveData<List<Article>> getArticles() {
        return articles;
    }
}
