package top.easelink.lcg.ui.main.article.viewmodel;

import top.easelink.framework.base.BaseViewModel;
import top.easelink.framework.utils.rx.SchedulerProvider;
import top.easelink.lcg.ui.main.article.view.ArticleNavigator;

public class ArticleViewModel extends BaseViewModel<ArticleNavigator> {

    public ArticleViewModel(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }
}
