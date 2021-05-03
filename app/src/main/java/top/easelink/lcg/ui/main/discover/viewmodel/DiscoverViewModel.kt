package top.easelink.lcg.ui.main.discover.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import top.easelink.framework.threadpool.IOPool
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.discover.model.DiscoverModel
import top.easelink.lcg.ui.main.discover.model.ForumListModel
import top.easelink.lcg.ui.main.discover.model.generateAllForums
import top.easelink.lcg.ui.main.discover.source.DateType
import top.easelink.lcg.ui.main.discover.source.RankType
import top.easelink.lcg.ui.main.discover.source.fetchRank
import top.easelink.lcg.utils.showMessage
import java.net.SocketTimeoutException

class DiscoverViewModel : ViewModel() {
    val aggregationModels = MutableLiveData<MutableList<DiscoverModel>>()

    suspend fun initOptions(context: Context) {
        aggregationModels.postValue(mutableListOf(ForumListModel(generateAllForums(context))))
        withContext(IOPool) {
            runCatching {
                fetchRank(RankType.HEAT, DateType.TODAY).let { ranks ->
                    ensureActive()
                    aggregationModels.value?.let {
                        it.add(ranks)
                        aggregationModels.postValue(it)
                    }
                }
            }.getOrElse {
                when (it) {
                    is SocketTimeoutException -> showMessage(R.string.network_error)
                    else -> showMessage(R.string.error)
                }
            }
        }
    }
}