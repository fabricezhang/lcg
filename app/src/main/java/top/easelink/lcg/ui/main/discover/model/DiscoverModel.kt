package top.easelink.lcg.ui.main.discover.model

sealed class DiscoverModel

class ForumListModel(val forumList: List<ForumNavigationModel>) : DiscoverModel()

data class RankListModel(
    val listModel: List<RankModel>,
    val notice: String
) : DiscoverModel()