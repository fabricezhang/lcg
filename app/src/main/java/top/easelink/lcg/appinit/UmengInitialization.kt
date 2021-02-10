package top.easelink.lcg.appinit

import android.content.Context
import com.umeng.analytics.MobclickAgent
import com.umeng.cconfig.RemoteConfigSettings
import com.umeng.cconfig.UMRemoteConfig
import com.umeng.commonsdk.UMConfigure
import top.easelink.lcg.BuildConfig


object UmengInitialization {

    fun init(context: Context) {
        initABTest()
        initUmengStatistic(context)
    }

    private fun initUmengStatistic(context: Context) {
        UMConfigure.setLogEnabled(BuildConfig.DEBUG)
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
        UMConfigure.init(
            context,
            BuildConfig.UMENG_APP_KEY,
            BuildConfig.CHANNEL,
            UMConfigure.DEVICE_TYPE_PHONE,
            ""
        )
    }

    private fun initABTest() {
        UMRemoteConfig.getInstance().setConfigSettings(
            RemoteConfigSettings.Builder().setAutoUpdateModeEnabled(true).build()
        )
    }

}