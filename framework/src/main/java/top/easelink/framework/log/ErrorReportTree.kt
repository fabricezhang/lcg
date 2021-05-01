package top.easelink.framework.log

import android.util.Log
import com.umeng.umcrash.UMCrash
import timber.log.Timber

class ErrorReportTree: Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority >= Log.ERROR) {
            UMCrash.generateCustomLog(t, tag)
        }
    }
}