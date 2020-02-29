package top.easelink.lcg.mta

import com.tencent.stat.StatService
import top.easelink.lcg.LCGApp
import java.util.*

fun sendEvent(key: String) {
    StatService.trackCustomEvent(LCGApp.context, key)
}

fun sendKVEvent(key: String, prop: Properties) {
    StatService.trackCustomKVEvent(LCGApp.context, key, prop)
}