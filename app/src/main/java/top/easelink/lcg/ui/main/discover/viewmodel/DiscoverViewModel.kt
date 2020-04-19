package top.easelink.lcg.ui.main.discover.viewmodel

import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import top.easelink.lcg.ui.main.discover.model.DiscoverModel
import top.easelink.lcg.ui.main.discover.model.ForumListModel
import top.easelink.lcg.ui.main.discover.model.ForumNavigationModel
import top.easelink.lcg.ui.main.discover.model.generateAllForums

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
    }
}