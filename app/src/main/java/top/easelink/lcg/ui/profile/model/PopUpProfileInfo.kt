package top.easelink.lcg.ui.profile.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PopUpProfileInfo(
    @SerializedName("image_x")
    val imageX: Int,
    @SerializedName("image_y")
    val imageY: Int,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("username")
    val userName: String,
    @SerializedName("extra_user_info")
    val extraUserInfo: String?,
    @SerializedName("follow_info")
    val followInfo: Pair<String, String>?, // title -> url
    @SerializedName("profile_url")
    val profileUrl: String?
): Parcelable