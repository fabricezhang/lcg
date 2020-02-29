package top.easelink.framework.guard

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Process
import timber.log.Timber

class AppGuard(
    private var mContext: Context,
    private val mDefaultUncaughtExceptionHandler: Thread.UncaughtExceptionHandler? = null
): Thread.UncaughtExceptionHandler{

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        restartApp(mContext)
        mDefaultUncaughtExceptionHandler?.uncaughtException(t, e)
        Process.killProcess(Process.myPid())
    }

    private fun restartApp(context: Context) {
        try {
            val packInfo =
                context.packageManager.getPackageInfo(
                    context.packageName, PackageManager.GET_UNINSTALLED_PACKAGES
                            or PackageManager.GET_ACTIVITIES
                )
            val activities = packInfo.activities
            if (activities != null && activities.isNotEmpty()) {
                val startActivity = activities[0]
                val intent = Intent()
                intent.setClassName(context.packageName, startActivity.name)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}