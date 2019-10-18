//package top.easelink.lcg.ui.main.me.viewmodel;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.databinding.BindingAdapter;
//import androidx.recyclerview.widget.RecyclerView;
//
//import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import timber.log.Timber;
//import top.easelink.framework.base.BaseViewHolder;
//import top.easelink.lcg.R;
//import top.easelink.lcg.databinding.ItemPostViewBinding;
//import top.easelink.lcg.ui.main.article.viewmodel.ArticleAdapterListener;
//import top.easelink.lcg.ui.main.article.viewmodel.ArticleViewModel;
//import top.easelink.lcg.ui.main.article.viewmodel.PostViewModel;
//import top.easelink.lcg.ui.main.me.model.NotificationInfo;
//import top.easelink.lcg.ui.main.source.model.Post;
//
//import static top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL;
//
//public class NotificationAdapter extends RecyclerView.Adapter<BaseViewHolder> {
//
//    private static final int VIEW_TYPE_EMPTY = 0;
//    private static final int VIEW_TYPE_NORMAL = 1;
//    private static final int VIEW_TYPE_LOAD_MORE = 2;
//    private List<NotificationInfo> mNotificationInfoList = new ArrayList<>();
//
//    @BindingAdapter("adapter")
//    public static void addPostItems(RecyclerView recyclerView, List<NotificationInfo> notificationInfoList) {
//        NotificationAdapter adapter = (NotificationAdapter) recyclerView.getAdapter();
//        if (adapter != null) {
//            adapter.clearItems();
//            adapter.addItems(notificationInfoList);
//        }
//    }
//
//    public NotificationAdapter(ArticleAdapterListener listener) {
//        mListener = listener;
//    }
//
//    @Override
//    public int getItemCount() {
//        if (mNotificationInfoList.isEmpty()) {
//            // show empty view
//            return 1;
//        } else {
//            return mNotificationInfoList.size() + 1;
//        }
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (mNotificationInfoList.isEmpty()) {
//            return VIEW_TYPE_EMPTY;
//        } else if (position == mNotificationInfoList.size()) {
//                return VIEW_TYPE_LOAD_MORE;
//        } else {
//            return VIEW_TYPE_NORMAL;
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
//        holder.onBind(position);
//    }
//
//    @Override
//    @NonNull
//    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        switch (viewType) {
//            case VIEW_TYPE_NORMAL:
//                ItemPostViewBinding itemPostViewBinding
//                        = ItemPostViewBinding.inflate(LayoutInflater.from(parent.getContext()),
//                        parent, false);
//                return new NotificationViewHolder(itemPostViewBinding);
//            case VIEW_TYPE_EMPTY:
//            default:
//                return new PostEmptyViewHolder(
//                        LayoutInflater.from(parent.getContext())
//                                .inflate(R.layout.layout_skelton_article
//                                        , parent, false));
//        }
//    }
//
//    private void addItems(List<NotificationInfo> notificationInfoList) {
//        mNotificationInfoList.addAll(notificationInfoList);
//        notifyDataSetChanged();
//    }
//
//    private void clearItems() {
//        mNotificationInfoList.clear();
//    }
//
//    public class NotificationViewHolder extends BaseViewHolder {
//
//        private ItemPostViewBinding mBinding;
//        private PostViewModel postViewModel;
//
//        NotificationViewHolder(ItemPostViewBinding binding) {
//            super(binding.getRoot());
//            this.mBinding = binding;
//        }
//
//        @Override
//        public void onBind(int position) {
//            final NotificationInfo notificationInfo = mNotificationInfoList.get(position);
//            postViewModel = new PostViewModel(ponotificationInfost);
//            mBinding.setViewModel(postViewModel);
//            try {
//                mBinding.contentTextView.setHtml(post.getContent(),htmlHttpImageGetter);
//            } catch (Exception e) {
//                Timber.e(e);
//            }
//            mBinding.executePendingBindings();
//        }
//    }
//
//    public class PostEmptyViewHolder extends BaseViewHolder{
//
//        PostEmptyViewHolder(View view) {
//            super(view);
//        }
//
//        @Override
//        public void onBind(int position) {
//
//        }
//    }
//}