package top.easelink.lcg.utils

import timber.log.Timber
import top.easelink.lcg.ui.main.source.local.SharedPreferencesHelper
import java.util.*

fun getCookies() = SharedPreferencesHelper
    .getCookieSp()
    .all
    .mapValues {
        it.value.toString()
    }


fun setCookies(cookieUrl: String?) {
    if (cookieUrl != null) {
        val cookies = cookieUrl.split(";")
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val itemList = ArrayList<SharedPreferencesHelper.SpItem<*>>()
        for (cookie in cookies) {
            val cookiePair = cookie.split("=".toRegex(), 2).toTypedArray()
            itemList.add(SharedPreferencesHelper.SpItem<String>(cookiePair[0], cookiePair[1]))
            Timber.i("%s = %s", cookiePair[0], cookiePair[1])
        }
        SharedPreferencesHelper.setPreferenceWithList(
            SharedPreferencesHelper.getCookieSp(),
            itemList
        )
    }
}