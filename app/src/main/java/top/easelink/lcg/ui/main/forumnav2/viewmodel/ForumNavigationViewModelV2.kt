package top.easelink.lcg.ui.main.forumnav2.viewmodel

import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import top.easelink.lcg.ui.main.forumnav.viewmodel.generateAllForums
import top.easelink.lcg.ui.main.forumnav2.view.BaseNavigationItem
import top.easelink.lcg.ui.main.forumnav2.view.ForumNavigationItem

class ForumNavigationViewModelV2 : ViewModel() {
    val navigationItems = MutableLiveData<List<BaseNavigationItem>>()

    @MainThread
    fun initOptions(context: Context) {
        navigationItems.value = generateAllForums(context).map {
            ForumNavigationItem(it)
        }
    }
}