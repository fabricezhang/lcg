package top.easelink.framework.log

import com.tencent.bugly.crashreport.CrashReport
import timber.log.Timber

class ErrorReportTree: Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority >= 6) {
            CrashReport.postCatchedException(t)
        }
    }
}