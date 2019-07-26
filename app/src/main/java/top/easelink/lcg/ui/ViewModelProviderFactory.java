package top.easelink.lcg.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.ui.main.about.viewmodel.AboutViewModel;
import top.easelink.lcg.ui.main.article.viewmodel.ArticleViewModel;
import top.easelink.lcg.ui.main.article.viewmodel.DownloadLinkViewModel;
import top.easelink.lcg.ui.main.articles.viewmodel.ArticlesViewModel;
import top.easelink.lcg.ui.main.articles.viewmodel.FavoriteArticlesViewModel;
import top.easelink.lcg.ui.main.articles.viewmodel.ForumArticlesViewModel;
import top.easelink.lcg.ui.main.viewmodel.MainViewModel;
import top.easelink.lcg.ui.search.viewmodel.SearchViewModel;
import top.easelink.lcg.ui.splash.viewmodel.SplashViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ViewModelProviderFactory extends ViewModelProvider.NewInstanceFactory {

    private SchedulerProvider schedulerProvider;

    @Inject
    public ViewModelProviderFactory(SchedulerProvider schedulerProvider) {
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ArticlesViewModel.class)) {
            return (T) new ArticlesViewModel(schedulerProvider);
        } else if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(schedulerProvider);
        } else if (modelClass.isAssignableFrom(AboutViewModel.class)) {
            return (T) new AboutViewModel(schedulerProvider);
        } else if (modelClass.isAssignableFrom(ArticleViewModel.class)) {
            return (T) new ArticleViewModel(schedulerProvider);
        } else if (modelClass.isAssignableFrom(ForumArticlesViewModel.class)) {
            return (T) new ForumArticlesViewModel(schedulerProvider);
        } else if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            return (T) new SearchViewModel(schedulerProvider);
        } else if (modelClass.isAssignableFrom(SplashViewModel.class)) {
            return (T) new SplashViewModel(schedulerProvider);
        } else if (modelClass.isAssignableFrom(DownloadLinkViewModel.class)) {
            return (T) new DownloadLinkViewModel(schedulerProvider);
        } else if (modelClass.isAssignableFrom(FavoriteArticlesViewModel.class)) {
            return (T) new FavoriteArticlesViewModel(schedulerProvider);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}