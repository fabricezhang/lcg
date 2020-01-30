package top.easelink.lcg.ui.main.forumnav.viewmodel

import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import top.easelink.lcg.ui.main.model.ForumNavigationModel

class ForumNavigationViewModel : ViewModel() {
    val navigation = MutableLiveData<List<ForumNavigationModel>>()

    @MainThread
    fun initOptions(context: Context) {
        navigation.value = generateAllForums(context)
    }
}