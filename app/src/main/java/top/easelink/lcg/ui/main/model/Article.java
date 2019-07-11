package top.easelink.lcg.ui.main.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * author : junzhang
 * date   : 2019-07-04 11:38
 * desc   :
 */
@Entity(tableName = "articles")
public class Article {
    @PrimaryKey(autoGenerate = true)
    private String mId;

    @NonNull
    @ColumnInfo(name = "title")
    private String mTitle;

    @NonNull
    @ColumnInfo(name = "author")
    private String mAuthor;

    @NonNull
    @ColumnInfo(name = "date")
    private String mDate;

    @NonNull
    @ColumnInfo(name = "view")
    private Integer mView;

    @NonNull
    @ColumnInfo(name = "reply")
    private Integer mReply;

    @NonNull
    @ColumnInfo(name = "url")
    private String mUrl;

    @NonNull
    @ColumnInfo(name = "origin")
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
    public String getId() {
        return mId;
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
