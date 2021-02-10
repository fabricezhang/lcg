package top.easelink.lcg.appinit

import android.app.Application
import android.content.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.DebugTree
import top.easelink.framework.guard.AppGuardStarter
import top.easelink.framework.log.ErrorReportTree
import top.easelink.framework.threadpool.BackGroundPool
import top.easelink.lcg.BuildConfig
import top.easelink.lcg.config.AppConfig
import top.easelink.lcg.service.work.SignInWorker


class LCGApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ErrorReportTree())
        }
        AppGuardStarter.init(this)
        BuglyInitialization.init(this)
        UmengInitialization.init(this)
        trySignIn()
    }

    private fun trySignIn() = GlobalScope.launch(BackGroundPool) {
        if (AppConfig.autoSignEnable) {
            delay(2000)
            try {
                SignInWorker.sendSignInRequest()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    companion object {
        lateinit var instance: LCGApp
            private set

        @JvmStatic
        val context: Context
            get() = instance

    }
}