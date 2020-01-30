package top.easelink.lcg.ui.main.forumnav2.view

import top.easelink.lcg.ui.main.model.ForumNavigationModel

sealed class BaseNavigationItem

class ForumNavigationItem(val forumNavigationModel: ForumNavigationModel): BaseNavigationItem()