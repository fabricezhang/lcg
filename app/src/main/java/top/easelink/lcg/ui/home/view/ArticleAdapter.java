package top.easelink.lcg.ui.home.view;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import timber.log.Timber;
import top.easelink.framework.base.BaseViewHolder;
import top.easelink.framework.customview.ScrollChildSwipeRefreshLayout;
import top.easelink.lcg.databinding.ItemArticleEmptyViewBinding;
import top.easelink.lcg.databinding.ItemArticleViewBinding;
import top.easelink.lcg.ui.home.model.Article;
import top.easelink.lcg.ui.home.viewmodel.ArticleEmptyItemViewModel;
import top.easelink.lcg.ui.home.viewmodel.ArticleItemViewModel;
import top.easelink.lcg.ui.home.viewmodel.HomeViewModel;

import javax.inject.Inject;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;

    private static final int VIEW_TYPE_NORMAL = 1;

    private List<Article> mArticleList;

    private ArticleAdapterListener mListener;

    @Inject
    public ArticleAdapter(List<Article> articleList) {
        this.mArticleList = articleList;
    }

    @BindingAdapter({"adapter"})
    public static void addArticleItems(RecyclerView recyclerView, List<Article> articles) {
        ArticleAdapter adapter = (ArticleAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.clearItems();
            adapter.addItems(articles);
        }
    }

    @BindingAdapter("android:onRefresh")
    public static void setRefreshListener(ScrollChildSwipeRefreshLayout view,
                                          final HomeViewModel viewModel) {
        view.setOnRefreshListener(viewModel::fetchArticles);
    }

    @Override
    public int getItemCount() {
        if (mArticleList != null && mArticleList.size() > 0) {
            return mArticleList.size();
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mArticleList != null && !mArticleList.isEmpty()) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    @NonNull
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                ItemArticleViewBinding articleViewBinding = ItemArticleViewBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new ArticleViewHolder(articleViewBinding);
            case VIEW_TYPE_EMPTY:
            default:
                ItemArticleEmptyViewBinding emptyViewBinding = ItemArticleEmptyViewBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new EmptyViewHolder(emptyViewBinding);
        }
    }

    public void addItems(List<Article> blogList) {
        mArticleList.addAll(blogList);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mArticleList.clear();
    }

    public void setListener(ArticleAdapterListener listener) {
        this.mListener = listener;
    }

    public interface ArticleAdapterListener {

        void onRetryClick();
    }

    public class ArticleViewHolder extends BaseViewHolder implements ArticleItemViewModel.ArticleItemViewModelListener {

        private ItemArticleViewBinding mBinding;

        private ArticleItemViewModel articleItemViewModel;

        ArticleViewHolder(ItemArticleViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            final Article article = mArticleList.get(position);
            articleItemViewModel = new ArticleItemViewModel(article, this);
            mBinding.setViewModel(articleItemViewModel);

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
        }

        @Override
        public void onItemClick(String blogUrl) {
            if (blogUrl != null) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(blogUrl));
                    itemView.getContext().startActivity(intent);
                } catch (Exception e) {
                    Timber.d("url error");
                }
            }
        }
    }

    public class EmptyViewHolder extends BaseViewHolder implements ArticleEmptyItemViewModel.ArticleEmptyItemViewModelListener {

        private ItemArticleEmptyViewBinding mBinding;

        EmptyViewHolder(ItemArticleEmptyViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            ArticleEmptyItemViewModel emptyItemViewModel = new ArticleEmptyItemViewModel(this);
            mBinding.setViewModel(emptyItemViewModel);
        }

        @Override
        public void onRetryClick() {
            if (mListener != null) {
                mListener.onRetryClick();
            }
        }
    }
}