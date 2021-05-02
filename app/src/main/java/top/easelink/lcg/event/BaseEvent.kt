package top.easelink.lcg.event

import com.umeng.analytics.MobclickAgent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.easelink.framework.threadpool.CalcPool
import top.easelink.lcg.appinit.LCGApp

abstract class BaseEvent {

    abstract val eventKey: String

    /**
     * 子类需将自身属性放入map中
     */
    open fun appendInfo(map: MutableMap<String, Any>) { }

    fun send() {
        GlobalScope.launch(CalcPool) {
            val info = mutableMapOf<String, Any>().also(::appendInfo)
            MobclickAgent.onEventObject(LCGApp.context, eventKey, info)
        }
    }
}