package top.easelink.lcg.ui.search.viewmodel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;
import top.easelink.framework.base.BaseViewHolder;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.ItemSearchResultViewBinding;
import top.easelink.lcg.ui.search.model.SearchResult;

import java.util.ArrayList;
import java.util.List;

import static top.easelink.lcg.ui.main.articles.viewmodel.ArticlesViewModel.FETCH_MORE;

public class SearchResultAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_LOAD_MORE = 2;

    private List<SearchResult> mSearchResults = new ArrayList<>();

    private SearchAdapterListener mListener;

    public SearchResultAdapter(SearchAdapterListener listener) {
        mListener = listener;
    }

    @BindingAdapter({"adapter"})
    public static void addResultItems(RecyclerView recyclerView, List<SearchResult> results) {
        SearchResultAdapter adapter = (SearchResultAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.clearItems();
            adapter.addItems(results);
        }
    }

    @Override
    public int getItemCount() {
        if (mSearchResults.isEmpty()) {
            return 1;
        } else {
            return mSearchResults.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mSearchResults.isEmpty()) {
            return VIEW_TYPE_EMPTY;
        } else {
            if (position == mSearchResults.size()) {
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
                ItemSearchResultViewBinding searchResultViewBinding
                        = ItemSearchResultViewBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new SearchResultViewHolder(searchResultViewBinding);
            case VIEW_TYPE_LOAD_MORE:
                return new LoadMoreViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_load_more_view
                                        , parent, false));
            case VIEW_TYPE_EMPTY:
            default:
                return new EmptyViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_load_more_view, parent, false));
        }
    }

    private void addItems(List<SearchResult> searchResults) {
        mSearchResults.addAll(searchResults);
        notifyDataSetChanged();
    }

    private void clearItems() {
        mSearchResults.clear();
    }

    public void setListener(SearchAdapterListener listener) {
        this.mListener = listener;
    }

    public interface SearchAdapterListener {
        int FETCH_INIT = 0;
        int FETCH_MORE = 1;
        void doSearchQuery(int type);
    }

    public class SearchResultViewHolder extends BaseViewHolder {

        private ItemSearchResultViewBinding mBinding;

        private SearchResultItemViewModel searchResultItemViewModel;

        SearchResultViewHolder(ItemSearchResultViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            final SearchResult searchResult = mSearchResults.get(position);
            searchResultItemViewModel = new SearchResultItemViewModel(searchResult);
            mBinding.setViewModel(searchResultItemViewModel);
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
            mListener.doSearchQuery(FETCH_MORE);
        }
    }
}