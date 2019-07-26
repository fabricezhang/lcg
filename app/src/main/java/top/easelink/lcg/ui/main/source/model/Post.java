package top.easelink.lcg.ui.main.source.model;

import androidx.annotation.NonNull;

/**
 * author : junzhang
 * date   : 2019-07-04 11:38
 * desc   :
 */
public class Post {
    @NonNull
    private String mDate;

    @NonNull
    private String mAuthor;

    @NonNull
    private String mAvatar;

    @NonNull
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
