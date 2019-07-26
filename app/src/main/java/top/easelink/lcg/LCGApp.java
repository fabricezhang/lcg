package top.easelink.lcg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;
import timber.log.Timber;
import top.easelink.framework.BuildConfig;
import top.easelink.lcg.di.component.DaggerAppComponent;
import top.easelink.lcg.service.web.WebViewWrapper;
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

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this);
        mContext = this;
        CalligraphyConfig.initDefault(mCalligraphyConfig);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        initRx();
        WebViewWrapper.init();
    }

    public static Context getContext() {
        return mContext;
    }

    private void initRx() {
        RxJavaPlugins.setErrorHandler(e -> {
            if (e instanceof UndeliverableException) {
                Timber.e(e);
            } else {
                Thread.currentThread()
                        .getUncaughtExceptionHandler()
                        .uncaughtException(Thread.currentThread(), e);
            }
        });
    }
}
