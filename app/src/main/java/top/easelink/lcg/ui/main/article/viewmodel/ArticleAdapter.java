package top.easelink.lcg.ui.main.article.viewmodel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import timber.log.Timber;
import top.easelink.framework.base.BaseViewHolder;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.ItemPostViewBinding;
import top.easelink.lcg.ui.main.source.model.Post;

import java.util.ArrayList;
import java.util.List;

import static top.easelink.lcg.ui.main.source.remote.ArticlesRemoteDataSource.SERVER_BASE_URL;

public class ArticleAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_LOAD_MORE = 2;
    private ArticleAdapterListener mListener;
    private List<Post> mPostList = new ArrayList<>();

    @BindingAdapter({"adapter"})
    public static void addPostItmes(RecyclerView recyclerView, List<Post> postListt) {
        ArticleAdapter adapter = (ArticleAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.clearItems();
            adapter.addItems(postListt);
        }
    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView imageView, String url){
        Glide.with(imageView.getContext()).load(url) .placeholder(R.drawable.ic_noavatar_middle) .into(imageView);
    }

    public ArticleAdapter(ArticleAdapterListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        if (mPostList.isEmpty()) {
            // show empty view
            return 1;
        } else {
            return mPostList.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mPostList.isEmpty()) {
            return VIEW_TYPE_EMPTY;
        } else if (position == mPostList.size()) {
                return VIEW_TYPE_LOAD_MORE;
        } else {
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
                ItemPostViewBinding itemPostViewBinding
                        = ItemPostViewBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new PostViewHolder(itemPostViewBinding);
            case VIEW_TYPE_LOAD_MORE:
                return new LoadMoreViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_load_more_view, parent, false));
            case VIEW_TYPE_EMPTY:
            default:
                return new PostEmptyViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.layout_skelton_article
                                        , parent, false));
        }
    }

    private void addItems(List<Post> postList) {
        mPostList.addAll(postList);
        notifyDataSetChanged();
    }

    private void clearItems() {
        mPostList.clear();
    }

    public class PostViewHolder extends BaseViewHolder {

        private ItemPostViewBinding mBinding;
        private PostViewModel postViewModel;
        private HtmlHttpImageGetter htmlHttpImageGetter;

        PostViewHolder(ItemPostViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.htmlHttpImageGetter = new HtmlHttpImageGetter(mBinding.contentTextView,
                    SERVER_BASE_URL, true);
        }

        @Override
        public void onBind(int position) {
            final Post post = mPostList.get(position);
            postViewModel = new PostViewModel(post);
            mBinding.setViewModel(postViewModel);
            try {
                mBinding.contentTextView.setHtml(post.getContent(),htmlHttpImageGetter);
            } catch (Exception e) {
                Timber.e(e);
            }
            mBinding.executePendingBindings();
        }
    }

    public class PostEmptyViewHolder extends BaseViewHolder{

        PostEmptyViewHolder(View view) {
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
            mListener.fetchArticlePost(ArticleViewModel.FETCH_MORE);
        }
    }
}