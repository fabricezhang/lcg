package top.easelink.lcg.ui.main.source.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * This response is extracted from
 * if <script type="application/ld+json"> ... </script>
 */
class ArticleAbstractResponse {
    @Expose
    @SerializedName("title")
    var title: String? = null
    @Expose
    @SerializedName("images")
    var images: List<String>? = null
    @Expose
    @SerializedName("description")
    var description: String? = null
}