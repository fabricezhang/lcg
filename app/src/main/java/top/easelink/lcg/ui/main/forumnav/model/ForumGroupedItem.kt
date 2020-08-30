package top.easelink.lcg.ui.main.forumnav.model

import com.google.gson.annotations.SerializedName
import top.easelink.framework.customview.linkagerv.bean.BaseGroupedItem
import java.io.Serializable

class ForumGroupedItem : BaseGroupedItem<ForumGroupedItem.ItemInfo> {
    constructor(isHeader: Boolean, header: String) : super(isHeader, header)
    constructor(item: ItemInfo) : super(item)

    class ItemInfo(
        title: String?,
        group: String?,
        @SerializedName("page_url")
        var pageUrl: String,
        @SerializedName("img_url")
        var imgUrl: String? = null,
        @SerializedName("content")
        var content: String? = null,
        @SerializedName("desc")
        var desc: String? = null,
        @SerializedName("children")
        var children: List<ChildForumItemInfo>? = null
    ) : BaseGroupedItem.ItemInfo(title, group)
}


data class ChildForumItemInfo(
    @SerializedName("page_url")
    var pageUrl: String,
    @SerializedName("img_url")
    var imgUrl: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("desc")
    var desc: String? = null
) : Serializable

