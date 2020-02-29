package top.easelink.lcg

import android.app.Application
import android.content.Context
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tencent.stat.StatService
import timber.log.Timber
import timber.log.Timber.DebugTree
import top.easelink.lcg.mta.EVENT_APP_LAUNCH
import top.easelink.lcg.mta.sendEvent

class LCGApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        initBulgy()
        initMTA()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    private fun initBulgy() {
        Bugly.init(applicationContext, BuildConfig.BUGLY_APP_ID, false)
        Beta.largeIconId = R.drawable.ic_noavatar_middle
        Beta.smallIconId = R.drawable.ic_noavatar_middle
        Beta.enableHotfix = false
    }

    private fun initMTA() {
        StatService.setContext(context)
        StatService.registerActivityLifecycleCallbacks(instance)
        sendEvent(EVENT_APP_LAUNCH)
    }



    companion object {
        lateinit var instance: LCGApp
            private set
        @JvmStatic
        val context: Context
            get() = instance

    }
}