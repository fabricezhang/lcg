package top.easelink.framework.threadpool

import android.os.Build
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.android.asCoroutineDispatcher
import java.lang.reflect.Constructor

val Main = Looper.getMainLooper().asHandler(true).asCoroutineDispatcher("EL-main")
val ImmediatePool by lazy { ELDispatcher(ELThreadPoolProvider.IMMEDIATE_EXECUTORS) }
val CalcPool by lazy { ELDispatcher(ELThreadPoolProvider.CALCULATE_EXECUTOR) }
val BackGroundPool by lazy { ELDispatcher(ELThreadPoolProvider.BACKGROUND_EXECUTOR) }
val IOPool by lazy { ELDispatcher(ELThreadPoolProvider.IO_EXECUTOR) }

private fun Looper.asHandler(async: Boolean): Handler {
    // Async support was added since API 16
    if (!async || Build.VERSION.SDK_INT < 16) {
        return Handler(this)
    }

    if (Build.VERSION.SDK_INT >= 28) {
        return Handler.createAsync(this)
    }

    val constructor: Constructor<Handler>
    try {
        constructor = Handler::class.java.getDeclaredConstructor(
            Looper::class.java,
            Handler.Callback::class.java,
            Boolean::class.javaPrimitiveType
        )
    } catch (e: NoSuchMethodException) {
        return Handler(this)
    }
    return constructor.newInstance(this, null, true)
}