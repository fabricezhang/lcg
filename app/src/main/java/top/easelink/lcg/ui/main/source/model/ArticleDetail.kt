package top.easelink.lcg.ui.main.source.model

class ArticleDetail(
    val articleTitle: String,
    val postList: List<Post>,
    val nextPageUrl: String,
    val fromHash: String,
    val articleAbstractResponse: ArticleAbstractResponse?
)