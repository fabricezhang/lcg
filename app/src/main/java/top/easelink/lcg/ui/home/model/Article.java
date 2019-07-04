package top.easelink.lcg.ui.home.model;

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
@Entity(tableName = "tasks")
public class Article {
    @PrimaryKey(autoGenerate = true)
    private String mId;

    @NonNull
    @ColumnInfo(name = "title")
    private String mTitle;

    @Nullable
    @ColumnInfo(name = "author")
    private String mAuthor;

    @NonNull
    @ColumnInfo(name = "date")
    private String mDate;

    @Nullable
    @ColumnInfo(name = "view")
    private Integer mView;

    @Nullable
    @ColumnInfo(name = "reply")
    private Integer mReply;

    /**
     * Use this constructor to specify a completed Task if the Task already has an id (copy of
     * another Task).
     *
     * @param title       title of the task
     * @param author      author of the task
     */
    public Article(@NonNull String title,
                   @NonNull String author,
                   @NonNull String date,
                   @NonNull Integer view,
                   @NonNull Integer reply) {
        mTitle = title;
        mAuthor = author;
        mDate = date;
        mView = view;
        mReply = reply;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getAuthor() {
        return mAuthor;
    }

    @NonNull
    public Integer getView() {
        return mView;
    }

    @Nullable
    public String getDate() {
        return mDate;
    }
}
