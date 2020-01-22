package top.easelink.lcg.ui.main.recommand.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import top.easelink.lcg.R;
import top.easelink.lcg.ui.main.articles.view.ArticlesFragment;
import top.easelink.lcg.ui.main.model.TabModel;

public class RecommendViewPagerAdapter extends FragmentPagerAdapter {

    private List<TabModel> tabModels;

    public RecommendViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        tabModels = new ArrayList<>();
        tabModels.add(new TabModel(context.getString(R.string.tab_title_hot), "hot"));
        tabModels.add(new TabModel(context.getString(R.string.tab_title_tech), "tech"));
        tabModels.add(new TabModel(context.getString(R.string.tab_title_digest), "digest"));
        tabModels.add(new TabModel(context.getString(R.string.tab_title_new_thread), "newthread"));
    }

    @Override
    @NonNull
    public Fragment getItem(int position) {
        return ArticlesFragment.newInstance(tabModels.get(position).getUrl(), false);
    }

    @Override
    public int getCount() {
        return tabModels.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabModels.get(position).getTitle();
    }
}