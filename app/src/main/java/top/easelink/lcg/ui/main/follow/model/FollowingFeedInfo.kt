package top.easelink.lcg.ui.main.follow.model

data class FeedInfo(
    val username: String,
    val avatar: String,
    val dateTime: String,
    val title: String,
    val articleUrl: String,
    val content: String,
    val forum: String,
    val quote: String,
    val images: List<String>?
)