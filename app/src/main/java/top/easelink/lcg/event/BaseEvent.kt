package top.easelink.lcg.event

import com.umeng.analytics.MobclickAgent
import top.easelink.lcg.appinit.LCGApp

abstract class BaseEvent {

    abstract val eventKey: String

    open fun appendInfo(map: MutableMap<String, Any>) { }

    fun send() {
        val info = mutableMapOf<String, Any>().also(::appendInfo)
        MobclickAgent.onEventObject(LCGApp.context, eventKey, info)
    }
}