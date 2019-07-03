package top.easelink.lcg.ui.about.viewmodel;

import top.easelink.framework.base.BaseViewModel;
import top.easelink.framework.utils.rx.SchedulerProvider;

public class AboutViewModel extends BaseViewModel<AboutNavigator> {

    public AboutViewModel(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }

    public void onNavBackClick() {
        getNavigator().goBack();
    }
}
