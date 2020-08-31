package top.easelink.lcg.ui.profile.source

import kotlinx.coroutines.withContext
import org.jsoup.nodes.Document
import top.easelink.framework.threadpool.IOPool
import top.easelink.lcg.network.JsoupClient

object ProfileSource {
    suspend fun getProfile(query: String) = withContext(IOPool) {
        JsoupClient.sendGetRequestWithQuery(query)
    }

    suspend fun parseUserInfo(doc: Document) {

    }

}
