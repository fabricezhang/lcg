package top.easelink.lcg.ui.search.view;

import androidx.fragment.app.Fragment;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import top.easelink.framework.BR;
import top.easelink.framework.base.BaseActivity;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.ActivityMainBinding;
import top.easelink.lcg.ui.ViewModelProviderFactory;
import top.easelink.lcg.ui.search.viewmodel.SearchViewModel;

import javax.inject.Inject;

public class SearchActivity extends BaseActivity<ActivityMainBinding, SearchViewModel> implements HasSupportFragmentInjector {

    @Inject
    ViewModelProviderFactory factory;
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    public SearchViewModel getViewModel() {
        return null;
    }
}
