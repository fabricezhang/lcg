package top.easelink.lcg.ui.main.articles.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.source.local.ArticlesLocalDataSource
import top.easelink.lcg.ui.main.source.model.ArticleEntity
import top.easelink.lcg.utils.showMessage

class FavoriteArticlesViewModel : ViewModel(), ArticleFetcher {
    val articles = MutableLiveData<List<ArticleEntity>>()
    val isLoading = MutableLiveData<Boolean>()

    //TODO add pagination later
    private var mCurrentPage = 0

    override fun fetchArticles(type: Int) {
        when (type) {
            ArticleFetcher.FETCH_MORE -> return
            ArticleFetcher.FETCH_INIT -> rewindPageNum()
            else -> rewindPageNum()
        }
        isLoading.value = true
        GlobalScope.launch(Dispatchers.IO) {
            try {
                ArticlesLocalDataSource.getAllFavoriteArticles().let {
                    if (!it.isNullOrEmpty()) {
                        articles.postValue(it)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
            isLoading.postValue(false)
        }
    }

    fun removeAllFavorites() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                if(ArticlesLocalDataSource.delAllArticlesFromFavorite()){
                    articles.postValue(emptyList())
                    showMessage(R.string.remove_all_favorites_successfully)
                } else {
                    showMessage(R.string.remove_all_favorites_failed)
                }
            } catch (e: Exception) {
                Timber.e(e)
                showMessage(R.string.remove_all_favorites_failed)
            }
        }
    }

    private fun rewindPageNum() {
        mCurrentPage = 0
    }

    private fun nextPage() {
        mCurrentPage++
    }
}