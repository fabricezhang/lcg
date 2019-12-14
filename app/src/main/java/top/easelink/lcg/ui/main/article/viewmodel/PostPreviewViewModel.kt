package top.easelink.lcg.ui.main.article.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.android.synthetic.main.dialog_post_preview.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.source.remote.ArticlesRemoteDataSource
import java.lang.Exception

class PostPreviewViewModel : ViewModel() {

    val author = MutableLiveData<String>()
    val avatar = MutableLiveData<String>()
    val date = MutableLiveData<String>()
    val content = MutableLiveData<String>()
    val loadFailed = MutableLiveData<Int>()

    fun initUrl(query: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val post = ArticlesRemoteDataSource.getPostPreview(query)
                if (post != null) {
                    post.let {
                        author.postValue(it.author)
                        avatar.postValue(it.avatar)
                        date.postValue(it.date)
                        content.postValue(it.content)
                    }
                } else {
                    loadFailed.postValue(R.string.preview_fail_info_post_deleted)
                }
            } catch (e: Exception) {
                loadFailed.postValue(R.string.preview_fail_info)
            }
        }

    }
}

