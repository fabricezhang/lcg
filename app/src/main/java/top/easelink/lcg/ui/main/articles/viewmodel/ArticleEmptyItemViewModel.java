package top.easelink.lcg.ui.main.articles.viewmodel;

public class ArticleEmptyItemViewModel {

    private ArticleEmptyItemViewModelListener mListener;

    public ArticleEmptyItemViewModel(ArticleEmptyItemViewModelListener listener) {
        this.mListener = listener;
    }

    public void onRetryClick() {
        mListener.onRetryClick();
    }

    public interface ArticleEmptyItemViewModelListener {

        void onRetryClick();
    }
}
