package top.easelink.lcg.ui.main.source.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
class HistoryEntity(
    @field:ColumnInfo(name = "title") var title: String,
    @field:ColumnInfo(name = "author") var author: String,
    @field:ColumnInfo(name = "url") var url: String,
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