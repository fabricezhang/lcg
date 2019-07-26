package top.easelink.lcg.ui.main.source.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * author : junzhang
 * date   : 2019-07-04 11:38
 * desc   :
 */
public class Article {

    @NonNull
    private String mTitle;

    @NonNull
    private String mAuthor;

    @NonNull
    private String mDate;

    @NonNull
    private Integer mView;

    @NonNull
    private Integer mReply;

    @NonNull
    private String mUrl;

    @NonNull
    private String mOrigin;

    /**
     *
     * @param title       title of the task
     * @param author      author of the task
     */
    public Article(@NonNull String title,
                   @NonNull String author,
                   @NonNull String date,
                   @NonNull String url,
                   @NonNull Integer view,
                   @NonNull Integer reply,
                   @Nullable String origin) {
        mTitle = title;
        mAuthor = author;
        mDate = date;
        mUrl = url;
        mView = view;
        mReply = reply;
        mOrigin = origin == null? "":origin;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    @NonNull
    public String getAuthor() {
        return mAuthor;
    }

    @Nullable
    public Integer getView() {
        return mView;
    }

    @Nullable
    public Integer getReply() {
        return mReply;
    }

    @NonNull
    public String getDate() {
        return mDate;
    }

    @NonNull
    public String getUrl() {
        return mUrl;
    }

    @NonNull
    public String getOrigin() {
        return mOrigin;
    }
}
