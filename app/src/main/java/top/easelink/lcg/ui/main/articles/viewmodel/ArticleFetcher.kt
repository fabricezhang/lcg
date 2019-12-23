package top.easelink.lcg.ui.main.articles.viewmodel

interface ArticleFetcher {

    fun fetchArticles(fetchType: FetchType, callback: (Boolean) -> Unit)

    enum class FetchType{
        FETCH_INIT,
        FETCH_MORE
    }
}