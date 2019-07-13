package top.easelink.lcg.ui.search.viewmodel;

import top.easelink.framework.base.BaseViewModel;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.ui.search.view.SearchNavigator;


public class SearchViewModel extends BaseViewModel<SearchNavigator> {

    public SearchViewModel(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }
}