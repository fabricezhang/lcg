package top.easelink.lcg.utils.avatar

import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import top.easelink.lcg.R
import top.easelink.lcg.appinit.LCGApp
import kotlin.math.abs

private val maleAvatarPool = listOf(
    R.drawable.ic_avatar_m_1,
    R.drawable.ic_avatar_m_2,
    R.drawable.ic_avatar_m_3,
    R.drawable.ic_avatar_m_4,
    R.drawable.ic_avatar_m_5,
    R.drawable.ic_avatar_m_6
)

fun getDefaultAvatar(url: String): Int {
    val target = abs(url.hashCode()) % maleAvatarPool.size
    return maleAvatarPool[target]
}

val PlaceholderDrawable = ColorDrawable(ContextCompat.getColor(LCGApp.context, R.color.slight_light_gray))