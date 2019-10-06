package top.easelink.lcg.di.component;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import top.easelink.lcg.LCGApp;
import top.easelink.lcg.di.builder.ActivityBuilder;
import top.easelink.lcg.di.module.AppModule;

@Singleton
@Component(modules = {AndroidInjectionModule.class,
        AppModule.class,
        ActivityBuilder.class})
public interface AppComponent {

    void inject(LCGApp app);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}
