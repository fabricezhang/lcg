package top.easelink.lcg.ui.main.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import top.easelink.lcg.ui.main.about.view.AboutFragment;
import top.easelink.lcg.ui.main.article.view.ArticleFragment;
import top.easelink.lcg.ui.main.article.view.DownloadLinkDialog;
import top.easelink.lcg.ui.main.articles.view.ArticlesFragment;
import top.easelink.lcg.ui.main.articles.view.FavoriteArticlesFragment;
import top.easelink.lcg.ui.main.articles.view.ForumArticlesFragment;
import top.easelink.lcg.ui.main.forumnav.view.ForumNavigationFragment;

@Module
public abstract class MainFragmentProvider {

    @ContributesAndroidInjector()
    abstract ArticlesFragment provideArticlesFragmentFactory();

    @ContributesAndroidInjector()
    abstract ForumArticlesFragment provideForumArticlesFragmentFactory();

    @ContributesAndroidInjector()
    abstract ArticleFragment provideArticleFragmentFactory();

    @ContributesAndroidInjector()
    abstract FavoriteArticlesFragment provideFavoriteArticleFragmentFactory();

    @ContributesAndroidInjector
    abstract AboutFragment provideAboutFragmentFactory();

    @ContributesAndroidInjector
    abstract ForumNavigationFragment provideForumNavigationFragmentFactory();

    @ContributesAndroidInjector
    abstract DownloadLinkDialog provideDownloadLinkDialog();
}
