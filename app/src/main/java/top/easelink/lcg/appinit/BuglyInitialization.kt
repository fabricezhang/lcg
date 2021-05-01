package top.easelink.lcg.appinit

import android.content.Context
import com.tencent.bugly.Bugly
import com.tencent.bugly.BuglyStrategy
import com.tencent.bugly.beta.Beta
import top.easelink.lcg.BuildConfig
import top.easelink.lcg.R

object BuglyInitialization {

    fun init(context: Context) {
        Bugly.init(
            context,
            BuildConfig.BUGLY_APP_ID, false,
            BuglyStrategy().apply {
                isBuglyLogUpload = false
            }
        )
        Beta.largeIconId = R.drawable.ic_noavatar_middle
        Beta.smallIconId = R.drawable.ic_noavatar_middle
        Beta.enableHotfix = false
    }
}