package top.easelink.lcg.utils

import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * String to Timestamp
 * @param date String
 * @return Timestamp
 */

fun toTimeStamp(date: String): Long {
    return try {
        SimpleDateFormat.getDateTimeInstance().parse(date).time
    } catch (e: ParseException) {
        System.currentTimeMillis()
    }
}