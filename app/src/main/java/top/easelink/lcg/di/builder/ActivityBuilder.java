package top.easelink.lcg.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import top.easelink.lcg.ui.about.di.AboutFragmentProvider;
import top.easelink.lcg.ui.main.di.MainFragmentProvider;
import top.easelink.lcg.ui.main.view.MainActivity;

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = {AboutFragmentProvider.class, MainFragmentProvider.class})
    abstract MainActivity bindMainActivity();

}
