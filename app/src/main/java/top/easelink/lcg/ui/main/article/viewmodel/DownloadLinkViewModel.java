package top.easelink.lcg.ui.main.article.viewmodel;

import top.easelink.framework.base.BaseViewModel;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.ui.main.article.view.DownloadLinkCallBack;

public class DownloadLinkViewModel extends BaseViewModel<DownloadLinkCallBack> {

    public DownloadLinkViewModel(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }

    public void onExitClick() {
        getNavigator().dismissDialog();
    }
}
