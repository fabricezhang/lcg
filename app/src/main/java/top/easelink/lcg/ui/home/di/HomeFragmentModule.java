package top.easelink.lcg.ui.home.di;

import androidx.recyclerview.widget.LinearLayoutManager;
import dagger.Module;
import dagger.Provides;
import top.easelink.lcg.ui.home.view.HomeFragment;

@Module
public class HomeFragmentModule {

    @Provides
    LinearLayoutManager provideLinearLayoutManager(HomeFragment fragment) {
        return new LinearLayoutManager(fragment.getActivity());
    }
}
