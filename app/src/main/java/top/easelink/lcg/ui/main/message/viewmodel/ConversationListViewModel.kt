package top.easelink.lcg.ui.main.message.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.easelink.lcg.ui.main.model.Conversation

class ConversationListViewModel : ViewModel() {

    val conversations = MutableLiveData<List<Conversation>>()
    val isLoading = MutableLiveData<Boolean>()

    fun fetchConversations() {
        isLoading.value = true
        GlobalScope.launch(Dispatchers.IO) {
            isLoading.postValue(false)
        }
    }

    fun fetchMore() {

    }

}