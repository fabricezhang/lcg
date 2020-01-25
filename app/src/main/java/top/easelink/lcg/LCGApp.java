package top.easelink.lcg;

import android.app.Application;
import android.content.Context;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.stat.StatService;

import timber.log.Timber;
import top.easelink.lcg.mta.EventHelperKt;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static top.easelink.lcg.mta.MTAConstantKt.EVENT_APP_LAUNCH;

public class LCGApp extends Application {

    private static LCGApp INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        initBulgy();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static Context getContext() {
        return INSTANCE;
    }
    public static LCGApp getInstance() {
        return INSTANCE;
    }

    private void initBulgy() {
        Bugly.init(getApplicationContext(), BuildConfig.BUGLY_APP_ID, false);
        Beta.largeIconId = R.drawable.ic_noavatar_middle;
        Beta.smallIconId = R.drawable.ic_noavatar_middle;
        Beta.enableHotfix = false;

        StatService.registerActivityLifecycleCallbacks(LCGApp.this);
        EventHelperKt.sendEvent(EVENT_APP_LAUNCH);
    }
}
