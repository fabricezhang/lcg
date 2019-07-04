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
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "entryid")
    private final String mId;

    @Nullable
    @ColumnInfo(name = "title")
    private final String mTitle;

    /**
     * Use this constructor to specify a completed Task if the Task already has an id (copy of
     * another Task).
     *
     * @param title       title of the task
     * @param id          id of the task
     */
    public Article(@Nullable String title,
                @NonNull String id) {
        mId = id;
        mTitle = title;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }
}
