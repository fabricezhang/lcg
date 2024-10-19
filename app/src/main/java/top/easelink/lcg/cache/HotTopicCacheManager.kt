package top.easelink.lcg.cache

import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import timber.log.Timber
import top.easelink.framework.threadpool.IOPool
import top.easelink.lcg.appinit.LCGApp
import java.io.File

object HotTopicCacheManager: ICacheManager {

    private const val SPLITERATOR = "-"
    private const val TIME_INTERVAL = 6 * 60 * 60 * 1000
    private val HOT_TOPIC_CACHE_FOLDER = "${LCGApp.context.cacheDir}/hot_topic_articles"

    /**
     * 将Url对应的内容存入磁盘缓存
     */
    suspend fun saveToDisk(rankType: String, dateType: String, timeStamp: Long, content: String) = withContext(IOPool) {
        checkDirs()
        val file = File(HOT_TOPIC_CACHE_FOLDER, toFileName(rankType, dateType, timeStamp))
        try {
            file.writeText(content)
        } catch (e: Exception) {
            Timber.e(e)
            file.delete()
        }
    }

    /**
     * 从磁盘中找到对应的数据内容
     */
    fun findTodayHotTopic(rankType: String, dateType: String): Document? {
        File(HOT_TOPIC_CACHE_FOLDER)
            .list { _, name ->
                name.contains(rankType)
                        && name.contains(dateType)
                        && (System.currentTimeMillis() - name.getTimeStamp() <= TIME_INTERVAL)
            }
            ?.firstOrNull()
            ?.let {
                try {
                    return Jsoup.parse(File(HOT_TOPIC_CACHE_FOLDER, it), "utf-8")
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        return null
    }

    /**
     * 清楚磁盘缓存
     */
    override suspend fun clearAllCaches() = withContext(IOPool) {
        checkDirs()
    }

    /**
     * 检查文件夹是否存在，已经是否有多余文件
     */
    private fun checkDirs() {
        val parent = File(HOT_TOPIC_CACHE_FOLDER)
        if (!parent.exists()) {
            parent.mkdirs()
        } else {
            // remove unused files
            parent.list()
                ?.asSequence()
                ?.filter {
                    try {
                        it.getTimeStamp() - System.currentTimeMillis() > TIME_INTERVAL
                    } catch (e: Exception) {
                        Timber.e(e)
                        false
                    }
                }
                ?.forEach {
                    File(it).delete()
                }
        }

    }

    private fun String.getTimeStamp(): Long {
        return this.split(SPLITERATOR)[2].toLong()
    }

    private fun toFileName(rankType: String, dateType: String, timeStamp: Long): String {
        return "$rankType$SPLITERATOR$dateType$SPLITERATOR$timeStamp"
    }
}
