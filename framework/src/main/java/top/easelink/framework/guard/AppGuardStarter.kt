package top.easelink.framework.guard

import android.content.Context
import androidx.annotation.MainThread
import top.easelink.framework.utils.isOnMainThread

object AppGuardStarter {

    @MainThread
    fun init(context: Context) {
        if (!isOnMainThread()) {
            throw IllegalStateException("must init on main thread and cannot be called twice!!")
        }
        Thread.setDefaultUncaughtExceptionHandler(
            AppGuard(context, Thread.getDefaultUncaughtExceptionHandler())
        )

    }
}