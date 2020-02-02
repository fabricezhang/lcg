package top.easelink.lcg.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.*

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
    if(isApplicationAvailable(context, WECHAT_PACKAGE_NAME)) {
        context.startActivity(Intent(ACTION_MAIN).apply {
            addCategory(CATEGORY_LAUNCHER)
            addFlags(FLAG_ACTIVITY_NEW_TASK)
            component = ComponentName(WECHAT_PACKAGE_NAME, "com.tencent.mm.ui.LauncherUI")
        })
    } else {
        showMessage("您没有安装微信，请先安装")
    }
}