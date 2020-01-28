package top.easelink.framework.threadpool

import android.os.Process
import timber.log.Timber
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class ElThreadFactory(
    private val mName: String,
    private val mPriority: ELThreadPriority
): ThreadFactory {

    private val sCount = AtomicInteger(0)

    override fun newThread(r: Runnable): Thread {
        val name = "$mName-${sCount.incrementAndGet()}"
        Timber.d("Create new thread: $name")
        return object : Thread(r, name) {
            override fun run() {
                val priority = when (mPriority) {
                    ELThreadPriority.LOW -> Process.THREAD_PRIORITY_BACKGROUND
                    ELThreadPriority.HIGH -> Process.THREAD_PRIORITY_DISPLAY
                    ELThreadPriority.IMMEDIATE -> Process.THREAD_PRIORITY_URGENT_DISPLAY
                    // ELThreadPriority.NORMAL
                    else -> Process.THREAD_PRIORITY_DEFAULT
                }
                Process.setThreadPriority(priority)
                super.run()
            }
        }
    }
}