package top.easelink.lcg.event.business

import com.google.gson.annotations.SerializedName
import top.easelink.lcg.event.BaseEvent
import top.easelink.lcg.event.EVENT_OPEN_ARTICLE

class OpenArticleEvent(
    @SerializedName("is_preview")
    val isPreview: Boolean = false
): BaseEvent() {

    override val eventKey = EVENT_OPEN_ARTICLE

    override fun appendInfo(map: MutableMap<String, Any>) {
        map["is_preview"] = isPreview
    }

}