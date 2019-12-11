package top.easelink.lcg.ui.main.articles.viewmodel

import androidx.lifecycle.MutableLiveData
import org.greenrobot.eventbus.EventBus
import top.easelink.lcg.ui.main.model.OpenArticleEvent
import top.easelink.lcg.ui.main.source.model.ArticleEntity

class FavoriteArticleItemViewModel internal constructor(private val articleEntity: ArticleEntity) {

    val title: MutableLiveData<String> = MutableLiveData(articleEntity.title)
    val author: MutableLiveData<String> = MutableLiveData(articleEntity.author)
    val content: MutableLiveData<String> = MutableLiveData(articleEntity.content)

    fun onItemClick() {
        val event = OpenArticleEvent(articleEntity.url)
        EventBus.getDefault().post(event)
    }

}