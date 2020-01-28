package top.easelink.framework.threadpool

import timber.log.Timber
import top.easelink.framework.BuildConfig
import java.util.concurrent.*

class ELThreadPoolExecutor constructor(
    corePoolSize: Int,
    maximumPoolSize: Int,
    keepAliveTime: Long,
    unit: TimeUnit,
    workQueue: BlockingQueue<Runnable>,
    factory: ThreadFactory = Executors.defaultThreadFactory(),
    handler: RejectedExecutionHandler = AbortPolicy()
): ThreadPoolExecutor(
    corePoolSize,
    maximumPoolSize,
    keepAliveTime,
    unit,
    workQueue,
    factory,
    handler
) {

    override fun afterExecute(r: Runnable?, t: Throwable?) {
        super.afterExecute(r, t)
        if (t == null && r is Future<*>) {
            if (r.isDone && !r.isCancelled) {
                try {
                    r.get()
                } catch (exe: ExecutionException) {
                    if (BuildConfig.DEBUG) {
                        throw RuntimeException(exe.cause)
                    } else {
                        Timber.e(exe)
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }


}