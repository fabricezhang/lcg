package top.easelink.lcg.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import top.easelink.lcg.ui.main.di.MainFragmentProvider;
import top.easelink.lcg.ui.main.view.MainActivity;
import top.easelink.lcg.ui.search.view.SearchActivity;

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = {MainFragmentProvider.class})
    abstract MainActivity bindMainActivity();
}
