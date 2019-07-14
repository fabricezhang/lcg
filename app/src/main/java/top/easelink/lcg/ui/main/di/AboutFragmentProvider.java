package top.easelink.lcg.ui.main.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import top.easelink.lcg.ui.main.about.view.AboutFragment;

@Module
public abstract class AboutFragmentProvider {

    @ContributesAndroidInjector
    abstract AboutFragment provideAboutFragmentFactory();
}
