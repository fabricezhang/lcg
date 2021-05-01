package top.easelink.lcg.ui.search.model

data class LCGSearchResults(
    val searchResultList: List<LCGSearchResultItem>
) {
    var totalResult: String? = null
}

data class LCGSearchResultItem(
    val title: String,
    val content: String,
    val url: String,
    val author: String,
    val date: String,
    val replyView: String,
    val forum: String
){
    val fullUrl:String
    get() = url.takeIf { it.startsWith("http") } ?: "https://www.52pojie.cn/$url"
}