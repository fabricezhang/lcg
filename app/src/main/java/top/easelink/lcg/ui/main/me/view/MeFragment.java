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
import top.easelink.lcg.ui.main.me.viewmodel.MeViewModel;
import top.easelink.lcg.ui.webview.view.WebViewActivity;

import static top.easelink.lcg.utils.WebsiteConstant.LOGIN_URL;
import static top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL;

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
        getViewModel().fetchUserInfoDirect();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void showLoginFragment() {
//        new LoginDialog().show(getBaseActivity().getSupportFragmentManager(), LoginDialog.class.getSimpleName());
        WebViewActivity.startWebViewWith(SERVER_BASE_URL + LOGIN_URL, getContext());
    }
}
