package top.easelink.lcg.event

import com.umeng.analytics.MobclickAgent
import top.easelink.lcg.appinit.LCGApp
import java.util.*

fun sendEvent(key: String) {
    MobclickAgent.onEventObject(LCGApp.context, key, emptyMap())
}

fun sendKVEvent(key: String, prop: Properties) {

}