package top.easelink.lcg.ui.profile.source

import kotlinx.coroutines.withContext
import org.jsoup.nodes.Document
import top.easelink.framework.threadpool.ApiPool
import top.easelink.lcg.network.Client

object ProfileSource {
    suspend fun getProfile(query: String) = withContext(ApiPool) {
        Client.sendGetRequestWithQuery(query)
    }

    suspend fun parseUserInfo(doc: Document) {

    }

}
