package top.easelink.lcg.ui.main.article.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.easelink.lcg.ui.main.source.remote.ArticlesRemoteDataSource

class PostPreviewViewModel : ViewModel() {

    val author = MutableLiveData<String>()
    val avatar = MutableLiveData<String>()
    val date = MutableLiveData<String>()
    val content = MutableLiveData<String>()

    fun initUrl(query: String) {
        GlobalScope.launch(Dispatchers.IO) {
            ArticlesRemoteDataSource.getPostPreview(query)?.let {
                author.postValue(it.author)
                avatar.postValue(it.avatar)
                date.postValue(it.date)
                content.postValue(it.content)
            }
        }

    }
}

