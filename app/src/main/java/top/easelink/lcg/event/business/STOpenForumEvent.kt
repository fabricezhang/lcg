package top.easelink.lcg.event.business

import com.google.gson.annotations.SerializedName
import top.easelink.lcg.event.BaseEvent
import top.easelink.lcg.event.EVENT_OPEN_FORUM
import top.easelink.lcg.event.PROP_FORUM_NAME

/**
 * 打开论坛板块的埋点
 */
class STOpenForumEvent(
    @SerializedName(PROP_FORUM_NAME)
    val forumName: String
): BaseEvent() {

    override val eventKey = EVENT_OPEN_FORUM

    override fun appendInfo(map: MutableMap<String, Any>) {
        map[PROP_FORUM_NAME] = forumName
    }

}