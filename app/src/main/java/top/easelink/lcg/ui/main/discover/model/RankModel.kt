package top.easelink.lcg.ui.main.discover.model

data class RankModel(
    val index: Int,
    val title: String,
    val url: String,
    val forum: String,
    val authorName: String,
    val authorUrl: String,
    val date: String,
    val num: Int // view/reply/favorite/hot
)