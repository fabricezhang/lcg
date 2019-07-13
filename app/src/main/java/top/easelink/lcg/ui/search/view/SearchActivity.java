package top.easelink.lcg.ui.search.view;

import androidx.fragment.app.Fragment;
import dagger.android.AndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import top.easelink.framework.base.BaseActivity;
import top.easelink.lcg.databinding.ActivityMainBinding;
import top.easelink.lcg.ui.search.viewmodel.SearchViewModel;

public class SearchActivity extends BaseActivity<ActivityMainBinding, SearchViewModel> implements HasSupportFragmentInjector {


    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return null;
    }

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public SearchViewModel getViewModel() {
        return null;
    }
}
