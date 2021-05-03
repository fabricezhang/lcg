package top.easelink.lcg.appinit

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.easelink.framework.threadpool.BackGroundPool
import top.easelink.lcg.preload.PreviewCacheManager

object CacheCleanerTask {

    fun clearCachesIfNeeded() {
        GlobalScope.launch(BackGroundPool) {
            delay(5000)
            PreviewCacheManager.clearAllCaches()
        }
    }
}