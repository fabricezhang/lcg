package top.easelink.lcg.ui.main.about.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import top.easelink.framework.BR;
import top.easelink.framework.base.BaseFragment;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.FragmentAboutBinding;
import top.easelink.lcg.ui.ViewModelProviderFactory;
import top.easelink.lcg.ui.main.about.viewmodel.AboutViewModel;

import javax.inject.Inject;

public class AboutFragment extends BaseFragment<FragmentAboutBinding, AboutViewModel> implements AboutNavigator {

    @Inject
    ViewModelProviderFactory factory;

    public static AboutFragment newInstance() {
        Bundle args = new Bundle();
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_about;
    }

    @Override
    public AboutViewModel getViewModel() {
        return ViewModelProviders.of(this, factory).get(AboutViewModel.class);
    }

    @Override
    public void goBack() {
        getBaseActivity().onFragmentDetached(getTag());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getViewModel().setNavigator(this);
    }
}
