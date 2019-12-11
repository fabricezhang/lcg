package top.easelink.lcg.ui.main.articles.viewmodel

import androidx.lifecycle.MutableLiveData
import org.greenrobot.eventbus.EventBus
import top.easelink.lcg.ui.main.model.OpenArticleEvent
import top.easelink.lcg.ui.main.source.model.Article

class ArticleItemViewModel internal constructor(private val article: Article) {
    val title: MutableLiveData<String> = MutableLiveData(article.title)
    val author: MutableLiveData<String> = MutableLiveData(article.author)
    val date: MutableLiveData<String> = MutableLiveData(article.date)
    val replyAndView: MutableLiveData<String>
    val origin: MutableLiveData<String>

    fun onItemClick() {
        val event = OpenArticleEvent(article.url)
        EventBus.getDefault().post(event)
    }

    init {
        val reply = if (article.reply != null) article.reply!! else 0
        val view = if (article.view != null) article.view!! else 0
        replyAndView = MutableLiveData("$reply / $view")
        origin = MutableLiveData(article.origin)
    }
}