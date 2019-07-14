package top.easelink.lcg.ui.splash.viewmodel;

import top.easelink.framework.base.BaseViewModel;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.ui.splash.view.SplashNavigator;


public class SplashViewModel extends BaseViewModel<SplashNavigator> {

    public SplashViewModel(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }

}