package top.easelink.lcg.ui.main.me.viewmodel;

import top.easelink.framework.base.BaseViewModel;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.ui.main.me.view.MeNavigator;

public class MeViewModel extends BaseViewModel<MeNavigator> {

    public MeViewModel(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }

}
