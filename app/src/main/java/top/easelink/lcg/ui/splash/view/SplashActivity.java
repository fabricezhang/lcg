package top.easelink.lcg.ui.splash.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import top.easelink.framework.BR;
import top.easelink.framework.base.BaseActivity;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.ActivitySplashBinding;
import top.easelink.lcg.ui.ViewModelProviderFactory;
import top.easelink.lcg.ui.main.view.MainActivity;
import top.easelink.lcg.ui.splash.viewmodel.SplashViewModel;

import javax.inject.Inject;

public class SplashActivity extends BaseActivity<ActivitySplashBinding, SplashViewModel> {

    @Inject
    ViewModelProviderFactory factory;

    private SplashViewModel mSplashViewModel;
    private ActivitySplashBinding mSplashBinding;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public SplashViewModel getViewModel() {
        mSplashViewModel = ViewModelProviders.of(this, factory).get(SplashViewModel.class);
        return mSplashViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mSplashBinding = getViewDataBinding();
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }
}
