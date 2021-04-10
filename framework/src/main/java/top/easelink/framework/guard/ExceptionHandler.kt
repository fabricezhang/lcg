package top.easelink.framework.guard

import com.umeng.umcrash.UMCrash
import top.easelink.framework.utils.debugDo

object ExceptionHandler {
    private const val DEFAULT_ERROR_TYPE = "GENERAL_ERROR"
    /**
     * Throw Exception directly if in Debug Mode, collect it and send to UmengSDK otherwise
     * @param t Throwable
     * @param type defined by user, help to identify the error
     */
    fun safeLogException(t: Throwable, type: String = DEFAULT_ERROR_TYPE) {
        debugDo { throw t }
        UMCrash.generateCustomLog(t, type);
    }

}