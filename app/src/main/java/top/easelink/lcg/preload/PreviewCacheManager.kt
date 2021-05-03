package top.easelink.lcg.preload

import android.content.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import timber.log.Timber
import top.easelink.framework.threadpool.IOPool
import top.easelink.lcg.appinit.LCGApp
import java.io.File

object PreviewCacheManager {

    private val PREVIEW_CACHE_FOLDER = "${LCGApp.context.cacheDir}/preview_articles"
    private const val CONFIG_FILE_NAME = "PreviewCacheConfig"

    /**
     * 将Url对应的内容存入磁盘缓存
     */
    fun saveToDisk(url: String, content: String) {
        GlobalScope.launch(IOPool) {
            checkDirs()
            val file = File(PREVIEW_CACHE_FOLDER, getCacheFileName(url))
            try {
                file.writeText(content)
            } catch (e: Exception) {
                Timber.e(e)
                file.delete()
            }
        }
    }

    /**
     * 从磁盘中找到对应的数据内容
     */
    fun findDocOrNull(url: String): Document? {
        val file: File? = findInDisk("$PREVIEW_CACHE_FOLDER/${getCacheFileName(url)}")
        if (file != null && file.exists()) {
            try {
                return Jsoup.parse(file, "utf-8")
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return null
    }

    /**
     * 清楚磁盘缓存
     */
    suspend fun clearAllCaches() {
        if (checkShouldCache()) {
            File(PREVIEW_CACHE_FOLDER).deleteRecursively()
        }
    }

    private fun checkDirs() {
        val parent = File(PREVIEW_CACHE_FOLDER)
        if (!parent.exists()) parent.mkdirs()
    }

    private fun findInDisk(url: String): File? {
        return if (isLocalFile(url)) {
            File(url)
        } else {
            null
        }
    }

    private fun isLocalFile(url: String): Boolean {
        return url.startsWith("/")
    }

    private fun getCacheFileName(url: String): String {
        return url.hashCode().toString()
    }

    private suspend fun checkShouldCache(): Boolean {
        return System.currentTimeMillis() - LCGApp.instance.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE).getLong("LastCleanTime", System.currentTimeMillis()) > 24 * 60 * 60_000
    }
}
