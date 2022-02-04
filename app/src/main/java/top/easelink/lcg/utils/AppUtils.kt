package top.easelink.lcg.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import top.easelink.lcg.R

fun isApplicationAvailable(context: Context, packageName: String): Boolean {
    context.packageManager.getInstalledPackages(0).forEach {
        if (packageName == it.packageName) {
            return true
        }
    }
    return false
}

const val WECHAT_PACKAGE_NAME = "com.tencent.mm"

fun startWeChat(context: Context) {
    if (isApplicationAvailable(context, WECHAT_PACKAGE_NAME)) {
        context.startActivity(Intent(ACTION_MAIN).apply {
            addCategory(CATEGORY_LAUNCHER)
            addFlags(FLAG_ACTIVITY_NEW_TASK)
            component = ComponentName(WECHAT_PACKAGE_NAME, "com.tencent.mm.ui.LauncherUI")
        })
    } else {
        showMessage(R.string.install_wechat_tips)
    }
}

fun getScreenWidthDp(context: Context): Int {
    val displayMetrics = context.resources.displayMetrics
    return (displayMetrics.widthPixels / displayMetrics.density).toInt()
}
fun getScreenHeightDp(context: Context): Int {
    val displayMetrics = context.resources.displayMetrics
    return (displayMetrics.heightPixels / displayMetrics.density).toInt()
}

fun getScreenWidth(context: Context): Int = context.resources.displayMetrics.widthPixels
fun getScreenHeight(context: Context): Int = context.resources.displayMetrics.heightPixels