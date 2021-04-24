package top.easelink.lcg.event

import com.umeng.analytics.MobclickAgent
import top.easelink.framework.utils.debugDo
import top.easelink.lcg.appinit.LCGApp

fun sendEvent(key: String) {
    MobclickAgent.onEvent(LCGApp.context, key)
}

fun sendKVEvent(key: String, params: MutableMap<String, Any>) {
    if (params.isEmpty()) {
        debugDo { throw IllegalArgumentException("params should not be empty") }
        MobclickAgent.onEvent(LCGApp.context, key)
    } else {
        MobclickAgent.onEventObject(LCGApp.context, key, params)
    }
}