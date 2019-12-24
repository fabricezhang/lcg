package top.easelink.lcg.ui.main.article.viewmodel

interface ArticleAdapterListener {
    fun fetchArticlePost(type: Int, callback: (Boolean) -> Unit)
    fun replyAdd(url: String)

    companion object {
        const val FETCH_POST_INIT = 0
        const val FETCH_POST_MORE = 1
    }
}