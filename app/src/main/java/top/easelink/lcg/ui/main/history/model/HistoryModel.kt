package top.easelink.lcg.ui.main.history.model

data class HistoryModel(
    val title: String,
    val author: String,
    val url: String,
    val timeStamp: Long,
    val content: String? = null
)