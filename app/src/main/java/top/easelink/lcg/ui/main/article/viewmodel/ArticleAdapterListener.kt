package top.easelink.lcg.ui.main.article.viewmodel

interface ArticleAdapterListener {
    fun fetchArticlePost(type: Int)
    fun replyAdd(url: String)
}