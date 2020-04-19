package top.easelink.lcg.ui.main.discover.model

sealed class DiscoverModel

class ForumListModel(val forumList: List<ForumNavigationModel>): DiscoverModel()