package top.easelink.lcg.ui.main.article.view;

import androidx.annotation.StringRes;

public interface ArticleNavigator {
    void handleError(Throwable t);
    void showMessage(@StringRes int resId);
    void scrollToTop();
}
