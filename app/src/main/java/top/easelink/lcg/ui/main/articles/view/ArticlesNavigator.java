package top.easelink.lcg.ui.main.articles.view;

import androidx.annotation.StringRes;

public interface ArticlesNavigator {
    void handleError(Throwable t);
    void scrollToTop();
    void showMessage(@StringRes int resId);
}
