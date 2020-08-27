package top.easelink.lcg.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * String to Timestamp
 * @param date String
 * @return Timestamp
 */

fun toTimeStamp(date: String): Long {
    return try {
        SimpleDateFormat.getDateTimeInstance().parse(date)?.time ?: System.currentTimeMillis()
    } catch (e: ParseException) {
        System.currentTimeMillis()
    }
}

fun String.toTimeStamp(): Long? {
    return runCatching {
        SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.CHINA)
            .parse(this)
            ?.time
    }.getOrNull()
}