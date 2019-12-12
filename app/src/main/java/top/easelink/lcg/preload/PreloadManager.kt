package top.easelink.lcg.preload

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import timber.log.Timber
import top.easelink.lcg.LCGApp
import java.io.File


object PreloadManager {

    private val PRELOAD_FOLDER = "${LCGApp.getContext().externalCacheDir}/articles"

    fun findDocOrNull(fileName: String): Document? {
        val file :File? = findInDisk("$PRELOAD_FOLDER/$fileName")
        if (file != null) {
            try {
                return Jsoup.parse(file, "utf-8")
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return null
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
}
