package top.easelink.lcg.ui.main.source.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * author : junzhang
 * date   : 2019-07-26 11:38
 * desc   :
 */
@Entity(tableName = "articles")
class ArticleEntity(
    @field:ColumnInfo(name = "title") var title: String,
    @field:ColumnInfo(name = "author") var author: String,
    @field:ColumnInfo(name = "url") var url: String,
    @field:ColumnInfo(name = "del_url") var delUrl: String = "",
    content: String,
    timestamp: Long
) {
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String

    @ColumnInfo(name = "content")
    var content = ""

    @ColumnInfo(name = "timestamp")
    var timestamp: Long

    init {
        this.content = content
        this.timestamp = timestamp
        id = url
    }
}