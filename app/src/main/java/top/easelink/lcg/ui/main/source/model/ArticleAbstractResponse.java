package top.easelink.lcg.ui.main.source.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * This response is extracted from
 * if <script type="application/ld+json"> ... </script>
 */
public class ArticleAbstractResponse {

    @Expose
    @SerializedName("title")
    public String title;

    @Expose
    @SerializedName("images")
    public List<String> images;

    @Expose
    @SerializedName("description")
    public String description;
}
