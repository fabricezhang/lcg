package top.easelink.lcg.ui.main.articles.source

import androidx.annotation.WorkerThread
import org.jsoup.nodes.Document
import top.easelink.lcg.network.Client
import top.easelink.lcg.ui.main.source.model.ArticleEntity
import top.easelink.lcg.utils.WebsiteConstant.GET_FAVORITE_QUERY
import top.easelink.lcg.utils.toTimeStamp

object FavoriteDataSource {
    @WorkerThread
    fun getAllRemoteFavorites(): List<ArticleEntity> {
        return parseFavorites(Client.sendRequestWithQuery(GET_FAVORITE_QUERY))
    }

    private fun parseFavorites(doc: Document): List<ArticleEntity> {
        doc.apply {
            return getElementById("favorite_ul")
                .children()
                .map {
//                    val delUrl = it.selectFirst("a.y").attr("href")
                    val dateAdded = it.selectFirst("span.xg1").text()
                    it.getElementsByAttribute("target").first().let { target ->
                        val title = target.text()
                        val articleUrl = target.attr("href")
                        return@map ArticleEntity(
                            title = title,
                            author = "",
                            url = articleUrl,
                            content = "",
                            timestamp = toTimeStamp(dateAdded)
                        )
                    }
                }
        }
    }
}
