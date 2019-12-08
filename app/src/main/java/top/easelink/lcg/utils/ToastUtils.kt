package top.easelink.lcg.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import top.easelink.lcg.LCGApp


fun showMessage(msg: String) {
    Toast.makeText(LCGApp.getContext(), msg, Toast.LENGTH_SHORT).show()
}

fun showMessage(@StringRes resource: Int) {
    Toast.makeText(LCGApp.getContext(), resource, Toast.LENGTH_SHORT).show()
}

fun showMessage(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}