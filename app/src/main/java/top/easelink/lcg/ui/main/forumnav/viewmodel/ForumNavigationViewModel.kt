package top.easelink.lcg.ui.main.forumnav.viewmodel

import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import top.easelink.lcg.R
import top.easelink.lcg.spipedata.UserData.loggedInState
import top.easelink.lcg.ui.main.model.ForumNavigationModel
import top.easelink.lcg.utils.WebsiteConstant
import java.util.*

class ForumNavigationViewModel : ViewModel() {
    private val navigation = MutableLiveData<List<ForumNavigationModel>>()

    @MainThread
    fun initOptions(context: Context) {
        val list: MutableList<ForumNavigationModel> = ArrayList(9)
        list.add(
            ForumNavigationModel(
                context.getString(R.string.forum_mobile_security),
                R.drawable.ic_mobile_24dp,
                WebsiteConstant.MOB_SECURITY_URL
            )
        )
        list.add(
            ForumNavigationModel(
                context.getString(R.string.forum_software),
                R.drawable.ic_important_devices_black_24dp,
                WebsiteConstant.SOFTWARE_URL
            )
        )
        list.add(
            ForumNavigationModel(
                context.getString(R.string.forum_original_release),
                R.drawable.ic_chart_24dp,
                WebsiteConstant.ORIGINAL_RELEASE_URL
            )
        )
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
                R.drawable.ic_movie_filter_black_24dp,
                WebsiteConstant.ANIMATION_RELEASE_URL
            )
        )
        list.add(
            ForumNavigationModel(
                context.getString(R.string.forum_reverse_resource),
                R.drawable.ic_find_replace_black_24dp,
                WebsiteConstant.REVERSE_RESOURCE_URL
            )
        )
        list.add(
            ForumNavigationModel(
                context.getString(R.string.forum_qa),
                R.drawable.ic_live_help_black_24dp,
                WebsiteConstant.QA_URL
            )
        )
        if (loggedInState) {
            list.add(
                ForumNavigationModel(
                    context.getString(R.string.forum_virus_analysis),
                    R.drawable.ic_report_problem_black_24dp,
                    WebsiteConstant.VIRUS_ANALYSIS_URL
                )
            )
            list.add(
                ForumNavigationModel(
                    context.getString(R.string.forum_virus_rescue),
                    R.drawable.ic_help_black_24dp,
                    WebsiteConstant.VIRUS_RESCUE_URL
                )
            )
            list.add(
                ForumNavigationModel(
                    context.getString(R.string.forum_virus_sample),
                    R.drawable.ic_bug_report_black_24dp,
                    WebsiteConstant.VIRUS_SAMPLE_URL
                )
            )
            list.add(
                ForumNavigationModel(
                    context.getString(R.string.free_chat_title),
                    R.drawable.ic_invert_colors_black_24dp,
                    WebsiteConstant.FREE_CHAT_URL
                )
            )
            list.add(
                ForumNavigationModel(
                    context.getString(R.string.welfare_title),
                    R.drawable.ic_attach_money_black_24dp,
                    WebsiteConstant.WELFARE_URL
                )
            )
        }
        navigation.value = list
    }

    fun getNavigation(): LiveData<List<ForumNavigationModel>> {
        return navigation
    }
}