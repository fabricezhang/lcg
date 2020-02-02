package top.easelink.lcg.utils

import java.text.SimpleDateFormat
import java.util.*

fun getDateFrom(timeStamp: Long): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(timeStamp)