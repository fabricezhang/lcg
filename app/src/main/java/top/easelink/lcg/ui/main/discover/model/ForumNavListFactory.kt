package top.easelink.lcg.ui.main.discover.model

import android.content.Context
import top.easelink.lcg.R
import top.easelink.lcg.spipedata.UserData
import top.easelink.lcg.utils.WebsiteConstant
import java.util.*

fun generateAllForums(context: Context): List<ForumNavigationModel> {
    val list: MutableList<ForumNavigationModel> = ArrayList()
    list.add(
        ForumNavigationModel(
            context.getString(R.string.forum_mobile_security),
            R.drawable.ic_sercurite,
            WebsiteConstant.MOB_SECURITY_URL
        )
    )
    list.add(
        ForumNavigationModel(
            context.getString(R.string.forum_software),
            R.drawable.ic_software,
            WebsiteConstant.SOFTWARE_URL
        )
    )
    list.add(
        ForumNavigationModel(
            context.getString(R.string.forum_original_release),
            R.drawable.ic_creator,
            WebsiteConstant.ORIGINAL_RELEASE_URL
        )
    )
    if (UserData.isLoggedIn) {
        list.add(
            ForumNavigationModel(
                context.getString(R.string.free_chat_title),
                R.drawable.ic_water,
                WebsiteConstant.FREE_CHAT_URL
            )
        )
        list.add(
            ForumNavigationModel(
                context.getString(R.string.welfare_title),
                R.drawable.ic_welfare,
                WebsiteConstant.WELFARE_URL
            )
        )
    }
    return list
}

fun generateOptionalList(context: Context): List<ForumNavigationModel> {
    val list: MutableList<ForumNavigationModel> = ArrayList()
    list.add(
        ForumNavigationModel(
            context.getString(R.string.forum_program_language),
            R.drawable.ic_message_black_24dp,
            WebsiteConstant.PROGRAM_LANGUAGE_URL
        )
    )
    list.add(
        ForumNavigationModel(
            context.getString(R.string.forum_animation_release),
            R.drawable.ic_message_black_24dp,
            WebsiteConstant.ANIMATION_RELEASE_URL
        )
    )
    list.add(
        ForumNavigationModel(
            context.getString(R.string.forum_reverse_resource),
            R.drawable.ic_message_black_24dp,
            WebsiteConstant.REVERSE_RESOURCE_URL
        )
    )
    list.add(
        ForumNavigationModel(
            context.getString(R.string.forum_qa),
            R.drawable.ic_message_black_24dp,
            WebsiteConstant.QA_URL
        )
    )
    if (UserData.isLoggedIn) {
        list.add(
            ForumNavigationModel(
                context.getString(R.string.forum_virus_analysis),
                R.drawable.ic_message_black_24dp,
                WebsiteConstant.VIRUS_ANALYSIS_URL
            )
        )
        list.add(
            ForumNavigationModel(
                context.getString(R.string.forum_virus_rescue),
                R.drawable.ic_message_black_24dp,
                WebsiteConstant.VIRUS_RESCUE_URL
            )
        )
        list.add(
            ForumNavigationModel(
                context.getString(R.string.forum_virus_sample),
                R.drawable.ic_message_black_24dp,
                WebsiteConstant.VIRUS_SAMPLE_URL
            )
        )
    }
    return list
}