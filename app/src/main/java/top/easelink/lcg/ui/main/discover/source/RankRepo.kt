package top.easelink.lcg.ui.main.discover.source

import org.jsoup.nodes.Document
import top.easelink.lcg.network.Client
import top.easelink.lcg.ui.main.discover.model.RankListModel
import top.easelink.lcg.ui.main.discover.model.RankModel
import top.easelink.lcg.utils.WebsiteConstant.RANK_QUERY


fun fetchRank(type: RankType, dateType: DateType): RankListModel {
    return parseRankModelInfo(
        Client.sendGetRequestWithQuery(RANK_QUERY.format(type.value, dateType.value)), type
    )
}

fun parseRankModelInfo(document: Document, rankType: RankType): RankListModel {
    return with(document) {
        val time = selectFirst("div.notice").text().orEmpty()
        val list = getElementsByTag("table")
            ?.select("tbody")
            ?.select("tr")
            ?.mapNotNull { tr ->
                try {
                    val index = tr.child(0).let {
                        if (it.childrenSize() > 0) {
                            it.child(0).attr("alt")
                        } else {
                            it.text()
                        }
                    }
                    val title = tr.child(1).text()
                    val url = tr.child(1).child(0).attr("href")
                    val forum = tr.child(2).text()
                    val authorName = tr.child(3).child(0).child(0).text()
                    val authorUrl = tr.child(3).child(0).child(0).attr("href")
                    val date = tr.child(3).child(1).text()
                    val num = tr.child(4).child(0).text()
                    RankModel(
                        title = title,
                        url = url,
                        authorName = authorName,
                        authorUrl = authorUrl,
                        date = date,
                        index = index.toInt(),
                        num = num.toInt(),
                        forum = forum,
                        type = rankType
                    )

                } catch (e: Exception) {
                    null
                }
            }
            .orEmpty()
        RankListModel(list, time)
    }
}

enum class DateType(val value: String) {
    TODAY("today"),
    ALL("all"),
    MONTH("thismonth"),
    WEEK("thisweek")
}

enum class RankType(val value: String) {
    VIEW("views"),
    REPLY("replies"),
    HEAT("heat"),
    FAVORITE("favtimes"),
    SHARE("sharetimes")
}