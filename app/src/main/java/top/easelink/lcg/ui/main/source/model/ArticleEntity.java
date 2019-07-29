package top.easelink.lcg.ui.main.source.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * author : junzhang
 * date   : 2019-07-26 11:38
 * desc   :
 */
@Entity(tableName = "articles")
public class ArticleEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @NonNull
    @ColumnInfo(name = "author")
    private String author;

    @NonNull
    @ColumnInfo(name = "url")
    private String url;

    @NonNull
    @ColumnInfo(name = "content")
    private String content = "";

    @NonNull
    @ColumnInfo(name = "timestamp")
    private Long timestamp;


    /**
     *
     * @param title       title of the task
     * @param author      author of the task
     */
    public ArticleEntity(@NonNull String title,
                         @NonNull String author,
                         @NonNull String url,
                         @NonNull String content,
                         @NonNull Long timestamp) {
        this.title = title;
        this.author = author;
        this.url = url;
        this.content = content;
        this.timestamp = timestamp;
        this.id = url;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getAuthor() {
        return author;
    }

    public void setAuthor(@NonNull String author) {
        this.author = author;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    @NonNull
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NonNull Long timestamp) {
        this.timestamp = timestamp;
    }
}
