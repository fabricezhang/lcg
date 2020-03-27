package top.easelink.lcg.ui.main.source.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * author : junzhang
 * date   : 2019-07-04 11:38
 * desc   :
 */
@Parcelize
class Post
/**
 *
 * @param author      author of the post
 * @param avatar      avatar of the author
 * @param date        datetime of the post
 * @param content     content of the post
 */(
    val author: String,
    val avatar: String,
    val date: String,
    val content: String,
    val replyUrl: String?,
    val replyAddUrl: String?,
    val profileUrl: String? = null,
    val extraInfo: String? = null
) : Parcelable