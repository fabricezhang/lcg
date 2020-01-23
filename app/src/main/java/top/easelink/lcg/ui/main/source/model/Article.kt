package top.easelink.lcg.ui.main.source.model

/**
 * author : junzhang
 * date   : 2019-07-04 11:38
 * desc   :
 */
class Article(
    val title: String,
    val author: String,
    val date: String,
    val url: String,
    val view: Int,
    val reply: Int,
    val origin: String = "",
    val helpCoin: Int = 0
)