package top.easelink.framework.threadpool

import java.util.concurrent.*
import kotlin.math.max
import kotlin.math.min

object ELThreadPoolProvider {

    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private val CORE_POOL_SIZE = max(4, min(CPU_COUNT + 1, 9))
    private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1
    private const val KEEP_ALIVE_SECONDS = 10L

    val IMMEDIATE_EXECUTORS = ELThreadPoolExecutor(
        0,
        Int.MAX_VALUE,
        0L,
        TimeUnit.SECONDS,
        SynchronousQueue<Runnable>(),
        ElThreadFactory("EL-immediate", ELThreadPriority.IMMEDIATE)
    )

    val API_EXECUTOR = ELThreadPoolExecutor(
        3,
        3,
        KEEP_ALIVE_SECONDS,
        TimeUnit.SECONDS,
        LinkedBlockingQueue<Runnable>(),
        ElThreadFactory("EL-api", ELThreadPriority.HIGH)
    )

    val COMMON_EXECUTOR = ELThreadPoolExecutor(
        CORE_POOL_SIZE,
        MAXIMUM_POOL_SIZE,
        KEEP_ALIVE_SECONDS,
        TimeUnit.SECONDS,
        LinkedBlockingQueue<Runnable>(),
        ElThreadFactory("EL-common", ELThreadPriority.NORMAL)
    )

    val BACKGROUND_EXECUTOR: ExecutorService =
        Executors.newSingleThreadExecutor(
            ElThreadFactory("EL-background", ELThreadPriority.LOW)
        )
}