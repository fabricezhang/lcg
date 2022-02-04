package top.easelink.lcg.utils

import android.webkit.CookieManager
import java.util.*

fun getCookies() = SharedPreferencesHelper
    .getCookieSp()
    .all
    .mapValues {
        it.value.toString()
    }


/**
 * 更新cookie
 * @param commit true -> 同步更新，false -> 异步更新
 */
fun updateCookies(cookieUrl: String, commit: Boolean) {
    val cookies = cookieUrl.split(";")
        .dropLastWhile { it.isEmpty() }
    val itemList = ArrayList<SharedPreferencesHelper.SpItem<*>>()
    for (cookie in cookies) {
        val cookiePair = cookie.split("=".toRegex(), 2).toTypedArray()
        itemList.add(SharedPreferencesHelper.SpItem<String>(cookiePair[0], cookiePair[1]))
    }
    if (commit) {
        SharedPreferencesHelper.commitPreferenceWithList(
            SharedPreferencesHelper.getCookieSp(),
            itemList
        )
    } else {
        SharedPreferencesHelper.setPreferenceWithList(
            SharedPreferencesHelper.getCookieSp(),
            itemList
        )
    }
}

fun updateCookies(cookies: Map<String, String>) {
    val itemList = ArrayList<SharedPreferencesHelper.SpItem<*>>()
    cookies.keys.forEach {
        itemList.add(SharedPreferencesHelper.SpItem<String>(it, cookies[it]))
    }
    SharedPreferencesHelper.setPreferenceWithList(
        SharedPreferencesHelper.getCookieSp(),
        itemList
    )
}

fun clearCookies() {
    SharedPreferencesHelper.getCookieSp().edit().clear().apply()
    CookieManager.getInstance().removeAllCookies(null)
}