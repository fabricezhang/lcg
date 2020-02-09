package top.easelink.lcg.ui.search.model

class BaiduSearchResults(val baiduSearchResultList: List<BaiduSearchResult>) {
    var totalResult: String? = null
    var nextPageUrl: String? = null
}

class BaiduSearchResult(
    val title: String,
    val contentAbstract: String,
    val url: String
)