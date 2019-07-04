package top.easelink.lcg.ui.about.viewmodel;

import top.easelink.framework.base.BaseViewModel;
import top.easelink.lcg.ui.about.view.AboutNavigator;

public class AboutViewModel extends BaseViewModel<AboutNavigator> {

    public AboutViewModel() {

    }

    public void onNavBackClick() {
        getNavigator().goBack();
    }
}
