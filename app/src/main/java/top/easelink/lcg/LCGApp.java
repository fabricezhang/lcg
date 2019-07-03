package top.easelink.lcg;

import android.app.Activity;
import android.app.Application;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import top.easelink.lcg.di.component.DaggerAppComponent;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import javax.inject.Inject;

public class LCGApp extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> activityDispatchingAndroidInjector;

    @Inject
    CalligraphyConfig mCalligraphyConfig;

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return activityDispatchingAndroidInjector;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this);

        CalligraphyConfig.initDefault(mCalligraphyConfig);
    }
}
