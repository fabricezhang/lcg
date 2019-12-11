package top.easelink.lcg.ui.main.articles.viewmodel

class ArticleEmptyItemViewModel internal constructor(private val mListener: ArticleEmptyItemViewModelListener) {
    fun onRetryClick() {
        mListener.onRetryClick()
    }

    interface ArticleEmptyItemViewModelListener {
        fun onRetryClick()
    }

}