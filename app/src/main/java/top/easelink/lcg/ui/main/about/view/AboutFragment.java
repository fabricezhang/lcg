package top.easelink.lcg.ui.main.about.view;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import top.easelink.framework.BR;
import top.easelink.framework.base.BaseFragment;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.FragmentAboutBinding;
import top.easelink.lcg.ui.ViewModelProviderFactory;
import top.easelink.lcg.ui.main.about.viewmodel.AboutViewModel;

import javax.inject.Inject;
import java.util.Calendar;

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

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        syncAuthorState();
    }

    private void syncAuthorState() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (0 <= hour && hour < 7) {
            getViewDataBinding().me.setAnimation(R.raw.moon_stars);
        } else if (7 <= hour && hour < 12) {
            getViewDataBinding().me.setAnimation(R.raw.personal_mac_daytime);
        } else if (12 <= hour && hour < 13) {
            getViewDataBinding().me.setAnimation(R.raw.sun);
        } else if (13 <= hour && hour < 18) {
            getViewDataBinding().me.setAnimation(R.raw.personal_phone_daytime);
        } else if (18 <= hour && hour < 22) {
            getViewDataBinding().me.setAnimation(R.raw.personal_mac_night);
        } else {
            getViewDataBinding().me.setAnimation(R.raw.personal_phone_night);
        }
    }
}
