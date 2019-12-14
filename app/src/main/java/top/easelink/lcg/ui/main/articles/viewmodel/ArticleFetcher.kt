package top.easelink.lcg.ui.main.articles.viewmodel

interface ArticleFetcher {
    fun fetchArticles(type: Int)

    companion object {
        const val FETCH_INIT = 0
        const val FETCH_MORE = 1
        const val FETCH_BY_THREAD = 2
    }
}