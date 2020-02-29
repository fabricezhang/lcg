package top.easelink.lcg.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import top.easelink.lcg.LCGApp


fun copyContent(content: String, label: String): Boolean {
    val cm = LCGApp.instance.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val mClipData = ClipData.newPlainText(label, content)
    return if (cm != null) {
        cm.primaryClip = mClipData
        true
    } else {
        false
    }
}