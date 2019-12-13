package top.easelink.lcg.ui.main.articles.viewmodel

import androidx.lifecycle.MutableLiveData
import top.easelink.lcg.ui.main.source.model.Article

class ArticleItemViewModel internal constructor(article: Article) {
    val title: MutableLiveData<String> = MutableLiveData(article.title)
    val author: MutableLiveData<String> = MutableLiveData(article.author)
    val date: MutableLiveData<String> = MutableLiveData(article.date)
    val replyAndView: MutableLiveData<String>
    val origin: MutableLiveData<String>

    init {
        val reply = article.reply
        val view = article.view
        replyAndView = MutableLiveData("$reply / $view")
        origin = MutableLiveData(article.origin)
    }
}