package top.easelink.lcg.utils.avatar

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import top.easelink.lcg.R
import kotlin.random.Random

private val maleAvatarPool = listOf(
    R.drawable.ic_avatar_m_1,
    R.drawable.ic_avatar_m_2,
    R.drawable.ic_avatar_m_3,
    R.drawable.ic_avatar_m_4,
    R.drawable.ic_avatar_m_5,
    R.drawable.ic_avatar_m_6
)

fun getAvatar(): Int {
    return maleAvatarPool[Random.nextInt(maleAvatarPool.size - 1)]
}

val PlaceholderDrawable = ColorDrawable(Color.LTGRAY)