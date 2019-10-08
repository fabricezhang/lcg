package top.easelink.lcg.ui.main.forumnav.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import top.easelink.framework.base.BaseViewModel;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.R;
import top.easelink.lcg.ui.main.forumnav.view.ForumNavigationNavigator;
import top.easelink.lcg.ui.main.model.ForumNavigationModel;

import static top.easelink.lcg.utils.WebsiteConstant.ANIMATION_RELEASE_URL;
import static top.easelink.lcg.utils.WebsiteConstant.FREE_CHAT_URL;
import static top.easelink.lcg.utils.WebsiteConstant.MOB_SECURITY_URL;
import static top.easelink.lcg.utils.WebsiteConstant.ORIGINAL_RELEASE_URL;
import static top.easelink.lcg.utils.WebsiteConstant.PROGRAM_LANGUAGE_URL;
import static top.easelink.lcg.utils.WebsiteConstant.QA_URL;
import static top.easelink.lcg.utils.WebsiteConstant.REVERSE_RESOURCE_URL;
import static top.easelink.lcg.utils.WebsiteConstant.SOFTWARE_URL;
import static top.easelink.lcg.utils.WebsiteConstant.VIRUS_ANALYSIS_URL;
import static top.easelink.lcg.utils.WebsiteConstant.VIRUS_RESCUE_URL;
import static top.easelink.lcg.utils.WebsiteConstant.VIRUS_SAMPLE_URL;

public class ForumNavigationViewModel extends BaseViewModel<ForumNavigationNavigator> {

    private final MutableLiveData<List<ForumNavigationModel>> navigation = new MutableLiveData<>();

    public ForumNavigationViewModel(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }

    public void initOptions(Context context) {
        List<ForumNavigationModel> list = new ArrayList<>(9);
        list.add(new ForumNavigationModel(
                context.getString(R.string.forum_mobile_security),
                R.drawable.ic_mobile_24dp,
                MOB_SECURITY_URL));
        list.add(new ForumNavigationModel(
                context.getString(R.string.forum_software),
                R.drawable.ic_important_devices_black_24dp,
                SOFTWARE_URL));
        list.add(new ForumNavigationModel(
                context.getString(R.string.forum_original_release),
                R.drawable.ic_chart_24dp,
                ORIGINAL_RELEASE_URL));
        list.add(new ForumNavigationModel(
                context.getString(R.string.forum_program_language),
                R.drawable.ic_message_black_24dp,
                PROGRAM_LANGUAGE_URL));
        list.add(new ForumNavigationModel(
                context.getString(R.string.forum_animation_release),
                R.drawable.ic_movie_filter_black_24dp,
                ANIMATION_RELEASE_URL));
        list.add(new ForumNavigationModel(
                context.getString(R.string.forum_reverse_resource),
                R.drawable.ic_find_replace_black_24dp,
                REVERSE_RESOURCE_URL));
        list.add(new ForumNavigationModel(
                context.getString(R.string.forum_qa),
                R.drawable.ic_live_help_black_24dp,
                QA_URL));
        list.add(new ForumNavigationModel(
                context.getString(R.string.forum_virus_analysis),
                R.drawable.ic_report_problem_black_24dp,
                VIRUS_ANALYSIS_URL));
        list.add(new ForumNavigationModel(
                context.getString(R.string.forum_virus_rescue),
                R.drawable.ic_help_black_24dp,
                VIRUS_RESCUE_URL));
        list.add(new ForumNavigationModel(
                context.getString(R.string.forum_virus_sample),
                R.drawable.ic_bug_report_black_24dp,
                VIRUS_SAMPLE_URL));
        list.add(new ForumNavigationModel(
                context.getString(R.string.free_chat_title),
                R.drawable.ic_invert_colors_black_24dp,
                FREE_CHAT_URL)
        );
        navigation.setValue(list);
    }

    public LiveData<List<ForumNavigationModel>> getNavigation() {
        return navigation;
    }
}
