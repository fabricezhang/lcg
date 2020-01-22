package top.easelink.lcg.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import top.easelink.lcg.ui.main.view.MainActivity;

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector()
    abstract MainActivity bindMainActivity();
}
