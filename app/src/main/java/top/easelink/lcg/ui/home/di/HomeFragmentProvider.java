package top.easelink.lcg.ui.home.di;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import top.easelink.lcg.ui.home.view.ArticleAdapter;
import top.easelink.lcg.ui.home.view.HomeFragment;

import java.util.ArrayList;

@Module
public abstract class HomeFragmentProvider {

    @ContributesAndroidInjector(modules = HomeFragmentModule.class)
    abstract HomeFragment provideHomeFragmentFactory();

}
