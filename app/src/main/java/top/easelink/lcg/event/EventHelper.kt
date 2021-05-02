package top.easelink.lcg.event

import com.umeng.analytics.MobclickAgent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.easelink.framework.threadpool.CalcPool
import top.easelink.framework.utils.debugDo
import top.easelink.lcg.appinit.LCGApp

private const val SINGLE_EVENT_KEY = "key"
/**
 * 为避免超过免费限额，这里使用一个事件，通过不同的类型，分别统计各个单独事件。
 */
fun sendSingleEvent(key: String) {
    GlobalScope.launch(CalcPool) {
        MobclickAgent.onEvent(LCGApp.context, EVENT_SINGLE_KEY_EVENT, mutableMapOf(SINGLE_EVENT_KEY to key))
    }
}

@Deprecated("正常情况下继承[BaseEvent]实现埋点上报")
fun sendKVEvent(key: String, params: MutableMap<String, out Any>) {
    if (params.isEmpty()) {
        debugDo { throw IllegalArgumentException("params should not be empty") }
        MobclickAgent.onEvent(LCGApp.context, key)
    } else {
        MobclickAgent.onEventObject(LCGApp.context, key, params)
    }
}