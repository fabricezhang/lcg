package top.easelink.lcg.ui.main.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * author : junzhang
 * date   : 2019-07-04 11:38
 * desc   :
 */
@Entity(tableName = "posts")
public class Post {
    @PrimaryKey(autoGenerate = true)
    private String mId;

    @NonNull
    @ColumnInfo(name = "date")
    private String mDate;

    @NonNull
    @ColumnInfo(name = "author")
    private String mAuthor;

    @NonNull
    @ColumnInfo(name = "date")
    private String mAvatar;

    @NonNull
    @ColumnInfo(name = "content")
    private String mContent;

    /**
     *
     * @param author      author of the post
     * @param avatar      avatar of the author
     * @param date        datetime of the post                   
     * @param content     content of the post
     */
    public Post(@NonNull String author,
                @NonNull String avatar,
                @NonNull String date,
                @NonNull String content) {        
        mAuthor = author;
        mAvatar = avatar;
        mContent = content;
        mDate = date;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getAuthor() {
        return mAuthor;
    }

    @NonNull
    public String getAvatar() {
        return mAvatar;
    }

    @NonNull
    public String getContent() {
        return mContent;
    }

    @NonNull
    public String getDate() {
        return mDate;
    }
}
