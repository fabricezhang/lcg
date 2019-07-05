package top.easelink.lcg.ui.main.articles.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import top.easelink.lcg.ui.main.articles.view.ArticlesFragment;

@Module
public abstract class ArticlesFragmentProvider {

    @ContributesAndroidInjector(modules = ArticlesFragmentModule.class)
    abstract ArticlesFragment provideHomeFragmentFactory();

}
