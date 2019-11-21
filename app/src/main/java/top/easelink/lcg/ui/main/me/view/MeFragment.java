package top.easelink.lcg.ui.main.me.view;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import javax.inject.Inject;

import top.easelink.framework.base.BaseFragment;
import top.easelink.lcg.BR;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.FragmentAboutBinding;
import top.easelink.lcg.ui.ViewModelProviderFactory;
import top.easelink.lcg.ui.main.login.view.LoginHintDialog;
import top.easelink.lcg.ui.main.me.viewmodel.MeViewModel;

public class MeFragment extends BaseFragment<FragmentAboutBinding, MeViewModel> implements MeNavigator {

    @Inject
    ViewModelProviderFactory factory;

    public static MeFragment newInstance() {
        Bundle args = new Bundle();
        MeFragment fragment = new MeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    public MeViewModel getViewModel() {
        return ViewModelProviders.of(this, factory).get(MeViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getViewModel().setNavigator(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getViewModel().fetchUserInfoDirect();
    }

    @Override
    public void showLoginFragment() {
        new LoginHintDialog().show(getBaseActivity().getSupportFragmentManager(), null);
//        new LoginDialog().show(getBaseActivity().getSupportFragmentManager(), LoginDialog.class.getSimpleName());
    }
}
