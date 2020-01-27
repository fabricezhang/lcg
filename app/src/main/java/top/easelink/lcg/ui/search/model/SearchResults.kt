package top.easelink.lcg.ui.search.model

class SearchResults(val searchResultList: List<SearchResult>) {
    var totalResult: String? = null
    var nextPageUrl: String? = null
}