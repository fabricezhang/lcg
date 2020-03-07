package top.easelink.lcg.ui.main.article.viewmodel;

import android.graphics.Bitmap;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import timber.log.Timber;
import top.easelink.framework.base.BaseViewHolder;
import top.easelink.framework.customview.htmltextview.DrawTableLinkSpan;
import top.easelink.framework.customview.htmltextview.HtmlGlideImageGetter;
import top.easelink.framework.utils.ScreenUtilsKt;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.ItemPostViewBinding;
import top.easelink.lcg.spipedata.UserData;
import top.easelink.lcg.ui.main.model.OpenArticleEvent;
import top.easelink.lcg.ui.main.model.OpenLargeImageViewEvent;
import top.easelink.lcg.ui.main.model.ReplyPostEvent;
import top.easelink.lcg.ui.main.model.ScreenCaptureEvent;
import top.easelink.lcg.ui.main.source.model.Post;
import top.easelink.lcg.ui.webview.view.WebViewActivity;
import top.easelink.lcg.utils.FileUtilsKt;

import static top.easelink.lcg.utils.CopyUtilsKt.copyContent;
import static top.easelink.lcg.utils.ToastUtilsKt.showMessage;
import static top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL;

public class ArticleAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_LOAD_MORE = 2;
    private ArticleAdapterListener mListener;
    private List<Post> mPostList = new ArrayList<>();

    public ArticleAdapter(ArticleAdapterListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        if (mPostList.isEmpty()) {
            // show empty view
            return 1;
        } else if (mPostList.size() > 10) {
            // for post more than 10 add a load more item
            return mPostList.size() + 1;
        } else {
            return mPostList.size();
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

    public void addItems(List<Post> postList) {
        mPostList.addAll(postList);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mPostList.clear();
    }

    public class PostViewHolder extends BaseViewHolder implements View.OnClickListener {

        private Post post;
        private ItemPostViewBinding mBinding;
        private Html.ImageGetter htmlHttpImageGetter;

        PostViewHolder(ItemPostViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
            htmlHttpImageGetter = new HtmlGlideImageGetter(mBinding.contentTextView.getContext(),
                    mBinding.contentTextView);
        }

        @Override
        public void onBind(int position) {
            post = mPostList.get(position);
            try {
                if (post.getAuthor().equals(UserData.INSTANCE.getUsername())) {
                    mBinding.postCard.setStrokeColor(
                            ContextCompat.getColor(
                                    mBinding.postCard.getContext(),
                                    R.color.orange
                            ));
                    mBinding.postCard.setStrokeWidth(
                            (int)ScreenUtilsKt.dp2px(mBinding.postCard.getContext(), 1f));
                } else  {
                    mBinding.postCard.setStrokeWidth(0);
                }
                mBinding.authorTextView.setText(post.getAuthor());
                mBinding.dateTextView.setText(post.getDate());
                Glide.with(mBinding.postAvatar)
                        .load(post.getAvatar())
                        .placeholder(R.drawable.ic_noavatar_middle)
                        .error(R.drawable.ic_noavatar_middle_gray)
                        .into(mBinding.postAvatar);
                mBinding.contentTextView.setClickableSpecialSpan(new ClickableSpecialSpanImpl());
                DrawTableLinkSpan drawTableLinkSpan = new DrawTableLinkSpan();
                drawTableLinkSpan.setTableLinkText(itemView.getContext().getString(R.string.tap_for_code));
                mBinding.contentTextView.setDrawTableLinkSpan(drawTableLinkSpan);
                mBinding.contentTextView.setImageTagClickListener(((c, imageUrl, pos) ->
                        EventBus.getDefault().post(new OpenLargeImageViewEvent(imageUrl))));
                mBinding.contentTextView.setOnLinkTagClickListener((c, url) -> {
                    if (url.startsWith(SERVER_BASE_URL+"thread")) {
                        EventBus.getDefault().post(new OpenArticleEvent(url.substring(SERVER_BASE_URL.length())));
                    } else {
                        WebViewActivity.startWebViewWith(url, c);
                    }
                });
                mBinding.contentTextView.setHtml(post.getContent(), htmlHttpImageGetter);
                if (position == 0) {
                    mBinding.btnCapture.setVisibility(View.VISIBLE);
                    mBinding.btnCapture.setOnClickListener(this);
                } else {
                    mBinding.btnCapture.setVisibility(View.GONE);
                }
                if (UserData.INSTANCE.isLoggedIn()) {
                    mBinding.btnGroup.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(post.getReplyUrl())) {
                        mBinding.btnReply.setVisibility(View.GONE);
                    } else {
                        mBinding.btnReply.setVisibility(View.VISIBLE);
                        mBinding.btnReply.setOnClickListener(this);
                    }
                    if (TextUtils.isEmpty(post.getReplyAddUrl())) {
                        mBinding.btnThumbUp.setVisibility(View.GONE);
                    } else {
                        mBinding.btnThumbUp.setVisibility(View.VISIBLE);
                        mBinding.btnThumbUp.setOnClickListener(this);
                    }
                    mBinding.btnCopy.setOnClickListener(this);
                } else {
                    mBinding.btnGroup.setVisibility(View.GONE);
                }
                mBinding.contentTextView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        // fix crashes on xiaomi devices
                        return true;
                    }
                });
            } catch (Exception e) {
                Timber.e(e);
            }
            mBinding.executePendingBindings();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_reply:
                    if (post.getReplyUrl() != null) {
                        EventBus.getDefault().post(new ReplyPostEvent(post.getReplyUrl(), post.getAuthor()));
                    }
                    break;
                case R.id.btn_copy:
                    if (copyContent(post.getContent(), post.getAuthor())) {
                        showMessage(R.string.copy_succeed);
                    } else {
                        showMessage(R.string.copy_failed);
                    }
                    break;
                case R.id.btn_thumb_up:
                    if (post.getReplyAddUrl() != null) {
                        mListener.replyAdd(post.getReplyAddUrl());
                    }
                    break;
                case R.id.btn_capture:
                    Bitmap bmp = ScreenUtilsKt.convertViewToBitmap(itemView, Bitmap.Config.ARGB_8888);
                    if (bmp != null) {
                        String path = FileUtilsKt.saveImageToGallery(bmp, String.valueOf(System.currentTimeMillis()));
                        EventBus.getDefault().post(new ScreenCaptureEvent(path));
                    } else {
                        showMessage(R.string.general_error);
                    }
                    break;
            }
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
        private View mView;

        LoadMoreViewHolder(View view) {
            super(view);
            mView = view;
        }

        @Override
        public void onBind(int position) {
            mListener.fetchArticlePost(ArticleAdapterListener.FETCH_POST_MORE, (res -> {
                mView.setVisibility(res? View.GONE: View.VISIBLE);
                return Unit.INSTANCE;
            }));
        }
    }
}