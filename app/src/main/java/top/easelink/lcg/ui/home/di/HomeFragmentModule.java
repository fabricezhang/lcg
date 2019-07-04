package top.easelink.lcg.ui.home.di;

import androidx.recyclerview.widget.LinearLayoutManager;
import dagger.Module;
import dagger.Provides;
import top.easelink.lcg.ui.home.view.ArticleAdapter;
import top.easelink.lcg.ui.home.view.HomeFragment;

import java.util.ArrayList;

@Module
public class HomeFragmentModule {

    @Provides
    ArticleAdapter provideArticleAdapter() {
        return new ArticleAdapter(new ArrayList<>());
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(HomeFragment fragment) {
        return new LinearLayoutManager(fragment.getActivity());
    }
}
