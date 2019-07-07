package top.easelink.lcg.ui.main.article.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import top.easelink.framework.base.BaseViewHolder;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.ItemPostViewBinding;
import top.easelink.lcg.ui.main.model.Post;
import top.easelink.lcg.ui.main.article.viewmodel.PostViewModel;

import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_LOAD_MORE = 2;

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
    public static void loadimage(ImageView imageView, String url){
        Glide.with(imageView.getContext()).load(url) .placeholder(R.mipmap.ic_launcher) .into(imageView);
    }


    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_NORMAL;
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
            default:
                ItemPostViewBinding itemPostViewBinding
                        = ItemPostViewBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new PostViewHolder(itemPostViewBinding);
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

        PostViewHolder(ItemPostViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            final Post post = mPostList.get(position);
            postViewModel = new PostViewModel(post);
            mBinding.setViewModel(postViewModel);
            mBinding.contentTextView.setHtml(post.getContent(), new HtmlHttpImageGetter(mBinding.contentTextView));
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
        }
    }
}