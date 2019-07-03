package top.easelink.lcg.ui.about.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import top.easelink.lcg.ui.about.view.AboutFragment;

@Module
public abstract class AboutFragmentProvider {

    @ContributesAndroidInjector
    abstract AboutFragment provideAboutFragmentFactory();
}
