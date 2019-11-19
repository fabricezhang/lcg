package top.easelink.lcg.ui.main.articles.viewmodel;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import top.easelink.framework.base.BaseViewHolder;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.ItemFavoriteArticleViewBinding;
import top.easelink.lcg.ui.main.source.local.ArticlesLocalDataSource;
import top.easelink.lcg.ui.main.source.model.ArticleEntity;

import static top.easelink.lcg.ui.main.articles.viewmodel.ArticlesViewModel.FETCH_MORE;

public class FavoriteArticlesAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_LOAD_MORE = 2;

    private List<ArticleEntity> mArticleEntities = new ArrayList<>();

    private ArticlesAdapterListener mListener;

    public FavoriteArticlesAdapter(ArticlesAdapterListener listener) {
        mListener = listener;
    }

    @BindingAdapter({"adapter"})
    public static void addFavoriteArticleItems(RecyclerView recyclerView, List<ArticleEntity> articleEntities) {
        FavoriteArticlesAdapter adapter = (FavoriteArticlesAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.clearItems();
            adapter.addItems(articleEntities);
        }
    }

    @Override
    public int getItemCount() {
        if (mArticleEntities.isEmpty()) {
            return 1;
        } else {
            return mArticleEntities.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mArticleEntities.isEmpty()) {
            return VIEW_TYPE_EMPTY;
        } else {
            if (position == mArticleEntities.size()) {
                return VIEW_TYPE_LOAD_MORE;
            }
            return VIEW_TYPE_NORMAL;
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
                ItemFavoriteArticleViewBinding favoriteArticleViewBinding
                        = ItemFavoriteArticleViewBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new ArticleViewHolder(favoriteArticleViewBinding, this);
            case VIEW_TYPE_LOAD_MORE:
                return new LoadMoreViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_load_more_view
                                        , parent, false));
            case VIEW_TYPE_EMPTY:
            default:
                return new EmptyViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_favorite_article_empty_view,
                                        parent, false));
        }
    }

    private void addItems(List<ArticleEntity> articleEntityList) {
        mArticleEntities.addAll(articleEntityList);
        notifyDataSetChanged();
    }

    private void clearItems() {
        mArticleEntities.clear();
    }

    public void setListener(ArticlesAdapterListener listener) {
        this.mListener = listener;
    }

    public interface ArticlesAdapterListener {
        int FETCH_INIT = 0;
        int FETCH_MORE = 1;
        void fetchArticles(int type);
    }

    public class ArticleViewHolder extends BaseViewHolder {

        private ItemFavoriteArticleViewBinding mBinding;
        private FavoriteArticlesAdapter mAdapter;

        private FavoriteArticleItemViewModel favoriteArticleItemViewModel;

        ArticleViewHolder(ItemFavoriteArticleViewBinding binding, FavoriteArticlesAdapter adapter) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.mAdapter = adapter;
        }

        @SuppressLint("CheckResult")
        @Override
        public void onBind(int position) {
            final ArticleEntity articleEntity = mArticleEntities.get(position);
            mBinding.removeButton.setOnClickListener(v -> ArticlesLocalDataSource.getInstance().delArticleFromFavorite(articleEntity.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(res -> {
                        if (res) {
                            mArticleEntities.remove(articleEntity);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, mArticleEntities.size() - position);
                        }
                    })
            );
            favoriteArticleItemViewModel = new FavoriteArticleItemViewModel(articleEntity);
            try {
                mBinding.contentTextView.setHtml(articleEntity.getContent().trim());
            } catch (Exception e) {
                Timber.e(e);
            }
            mBinding.setViewModel(favoriteArticleItemViewModel);
            mBinding.executePendingBindings();
        }
    }

    public class EmptyViewHolder extends BaseViewHolder {

        EmptyViewHolder(View view) {
            super(view);
        }

        @Override
        public void onBind(int position) {
        }
    }

    public class LoadMoreViewHolder extends BaseViewHolder{

        LoadMoreViewHolder(View view) {
            super(view);
        }

        @Override
        public void onBind(int position) {
            mListener.fetchArticles(FETCH_MORE);
        }
    }
}