package top.easelink.lcg.ui.main.model

import androidx.annotation.DrawableRes

class ForumNavigationModel(
    val title: String,
    @field:DrawableRes val drawableRes: Int,
    val url: String,
    val description: String? = null
)