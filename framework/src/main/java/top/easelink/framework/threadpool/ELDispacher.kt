package top.easelink.framework.threadpool

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.Runnable
import top.easelink.framework.BuildConfig
import java.util.concurrent.ExecutorService
import java.util.concurrent.RejectedExecutionException
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

class ELDispacher constructor(private val executor: ExecutorService): CoroutineDispatcher() {


    override fun dispatch(context: CoroutineContext, block: Runnable) {
        try {
            executor.execute(block)
        } catch (e: RejectedExecutionException) {

        }
    }

    override fun plus(context: CoroutineContext): CoroutineContext {
        if (BuildConfig.DEBUG) {
            if (context[ContinuationInterceptor] is MainCoroutineDispatcher) {
                throw UnsupportedOperationException("plus main is meaningless")
            }
        }
        return super.plus(context)
    }
}