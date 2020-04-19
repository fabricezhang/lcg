package top.easelink.lcg.ui.main.discover.viewmodel

import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.easelink.framework.threadpool.ApiPool
import top.easelink.lcg.ui.main.discover.model.DiscoverModel
import top.easelink.lcg.ui.main.discover.model.ForumListModel
import top.easelink.lcg.ui.main.discover.model.ForumNavigationModel

import top.easelink.lcg.ui.main.discover.model.generateAllForums
import top.easelink.lcg.ui.main.discover.source.DateType
import top.easelink.lcg.ui.main.discover.source.RankType
import top.easelink.lcg.ui.main.discover.source.fetchRank

class DiscoverViewModel : ViewModel() {
    val aggregationModels = MutableLiveData<MutableList<DiscoverModel>>()

    @MainThread
    fun initOptions(context: Context) {
        val list = generateAllForums(context)
            .fold(mutableListOf<ForumNavigationModel>()){ acc, model ->
                acc.also {
                    it.add(model)
                }
            }
        aggregationModels.value = mutableListOf(ForumListModel(list))
        GlobalScope.launch(ApiPool) {
            fetchRank(RankType.HEAT, DateType.TODAY).let { ranks ->
                aggregationModels.value?.let {
                    it.add(ranks)
                    aggregationModels.postValue(it)
                }
            }
        }
    }
}