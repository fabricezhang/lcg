package top.easelink.lcg.ui.main.message.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.nodes.Document
import timber.log.Timber
import top.easelink.framework.threadpool.IOPool
import top.easelink.lcg.network.Client
import top.easelink.lcg.ui.main.model.Conversation
import top.easelink.lcg.utils.WebsiteConstant

class ConversationListViewModel : ViewModel() {

    val conversations = MutableLiveData<List<Conversation>>()
    val isLoading = MutableLiveData<Boolean>()

    private var formHash: String? = null

    fun fetchConversations() {
        isLoading.value = true
        GlobalScope.launch(IOPool) {
            try {
                parseConversations(Client.sendGetRequestWithQuery(WebsiteConstant.PRIVATE_MESSAGE_QUERY))
            } catch (e: Exception) {
                Timber.e(e)
            }
            isLoading.postValue(false)
        }
    }

    private fun parseConversations(doc: Document) {
        doc.apply {
            formHash = selectFirst("input[name=formhash]")?.attr("value")
            val conversationList = select("dl[id^=pmlist]").map {
                val avatarUrl = it.selectFirst("img[onerror]")?.attr("src")
                val username = it.selectFirst("dd.ptm > a[target]")?.text().orEmpty()
                val lastMessage =  it.selectFirst("dd.ptm").textNodes().let { node->
                    if (node.size > 5) {
                        node[4].text()?.replaceFirst(" ", "").orEmpty()
                    } else {
                        ""
                    }
                }
                val datetime = it.selectFirst("span.xg1")?.text().orEmpty()
                val replyUrl = it.selectFirst("a[id^=pmlist]")?.attr("href")
                Conversation(
                    avatar = avatarUrl,
                    lastMessage = lastMessage,
                    lastMessageDateTime = datetime,
                    username = username,
                    totalMessage = null,
                    replyUrl = replyUrl
                )
            }
            conversations.postValue(conversationList)
        }

    }

}