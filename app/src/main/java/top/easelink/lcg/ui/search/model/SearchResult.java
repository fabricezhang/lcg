package top.easelink.lcg.ui.search.model;

import androidx.annotation.NonNull;

/**
 * author : junzhang
 * date   : 2019-07-14 11:38
 * desc   :
 */
public class SearchResult {

    private String mTitle;

    private String mContentAbstract;

    private String mUrl;

    public SearchResult(@NonNull String title,
                   @NonNull String date,
                   @NonNull String url) {
        mTitle = title;
        mContentAbstract = date;
        mUrl = url;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    @NonNull
    public String getContentAbstractt() {
        return mContentAbstract;
    }

    @NonNull
    public String getUrl() {
        return mUrl;
    }
}
