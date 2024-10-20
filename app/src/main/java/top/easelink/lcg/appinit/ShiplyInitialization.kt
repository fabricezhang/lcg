package top.easelink.lcg.appinit

import android.content.Context
import android.os.Build
import com.tencent.upgrade.bean.UpgradeConfig
import com.tencent.upgrade.callback.Logger
import com.tencent.upgrade.core.DefaultUpgradeStrategyRequestCallback
import com.tencent.upgrade.core.UpgradeManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import top.easelink.framework.threadpool.BackGroundPool
import top.easelink.lcg.BuildConfig
import top.easelink.lcg.account.UserDataRepo


object ShiplyInitialization {

    fun init(context: Context) {
        val config = UpgradeConfig.Builder()
            .appId(BuildConfig.SHIPLY_APP_ID)
            .appKey(BuildConfig.SHIPLY_APP_KEY)
            .isDebugPackage(BuildConfig.DEBUG)
            .apply {
                if (UserDataRepo.isLoggedIn) {
                    userId(UserDataRepo.username)
                }
            }
            .customLogger(object : Logger {
                override fun v(p0: String?, p1: String?) {
                    Timber.tag(p0).v(p1)
                }

                override fun v(p0: String?, p1: String?, p2: Throwable?) {
                    Timber.tag(p0).v(p2, p1)
                }

                override fun d(p0: String?, p1: String?) {
                    Timber.tag(p0).d(p1)
                }

                override fun d(p0: String?, p1: String?, p2: Throwable?) {
                    Timber.tag(p0).d(p2, p1)
                }

                override fun i(p0: String?, p1: String?) {
                    Timber.tag(p0).i(p1)
                }

                override fun i(p0: String?, p1: String?, p2: Throwable?) {
                    Timber.tag(p0).i(p2, p1)
                }

                override fun w(p0: String?, p1: String?) {
                    Timber.tag(p0).w(p1)
                }

                override fun w(p0: String?, p1: String?, p2: Throwable?) {
                    Timber.tag(p0).w(p2, p1)
                }

                override fun e(p0: String?, p1: String?) {
                    Timber.tag(p0).e(p1)
                }

                override fun e(p0: String?, p1: String?, p2: Throwable?) {
                    Timber.tag(p0).e(p2, p1)
                }

            })
            .systemVersion(Build.VERSION.SDK_INT.toString())
            .build()
        UpgradeManager.getInstance().init(context, config)
        GlobalScope.launch(BackGroundPool) {
            UpgradeManager.getInstance().checkUpgrade(false, null, DefaultUpgradeStrategyRequestCallback())
        }
    }
}