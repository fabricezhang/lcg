package top.easelink.lcg.ui.main.articles.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.articles.source.FavoriteDataSource.getAllRemoteFavorites
import top.easelink.lcg.ui.main.source.ArticlesDataSource
import top.easelink.lcg.ui.main.source.local.ArticlesDao
import top.easelink.lcg.ui.main.source.local.ArticlesLocalDataSource
import top.easelink.lcg.ui.main.source.model.ArticleEntity
import top.easelink.lcg.utils.showMessage

class FavoriteArticlesViewModel : ViewModel(), ArticleFetcher {
    val articles = MutableLiveData<List<ArticleEntity>>()
    val isLoading = MutableLiveData<Boolean>()

    //TODO add pagination later
    private var mCurrentPage = 0

    override fun fetchArticles(fetchType: ArticleFetcher.FetchType) {
        when (fetchType) {
            ArticleFetcher.FetchType.FETCH_MORE -> return
            ArticleFetcher.FetchType.FETCH_INIT -> rewindPageNum()
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
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun removeAllFavorites() {
        isLoading.value = true
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
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun syncFavorites() {
        isLoading.value = true
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val articleEntities = getAllRemoteFavorites()
                if(ArticlesLocalDataSource.addAllArticleToFavorite(articleEntities)) {
                    articles.postValue(ArticlesLocalDataSource.getAllFavoriteArticles())
                }
            } catch (e: Exception) {
                Timber.e(e)
                showMessage(R.string.sync_favorite_failed)
            } finally {
                isLoading.postValue(false)
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