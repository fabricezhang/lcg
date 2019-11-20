package top.easelink.lcg.ui.main.article.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import top.easelink.framework.base.BaseFragment;
import top.easelink.lcg.BR;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.FragmentArticleBinding;
import top.easelink.lcg.mta.EventHelperKt;
import top.easelink.lcg.ui.ViewModelProviderFactory;
import top.easelink.lcg.ui.main.article.viewmodel.ArticleAdapter;
import top.easelink.lcg.ui.main.article.viewmodel.ArticleViewModel;
import top.easelink.lcg.ui.main.model.ReplyPostEvent;
import top.easelink.lcg.ui.webview.view.WebViewActivity;

import static top.easelink.lcg.mta.MTAConstantKt.EVENT_OPEN_ARTICLE;
import static top.easelink.lcg.ui.main.article.viewmodel.ArticleViewModel.FETCH_INIT;
import static top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL;


public class ArticleFragment extends BaseFragment<FragmentArticleBinding, ArticleViewModel>
        implements ArticleNavigator {

    @Inject
    ViewModelProviderFactory factory;
    private static final String KEY_URL = "KEY_URL";
    private LinearLayoutManager mLayoutManager;
    private String articleUrl;

    public static ArticleFragment newInstance(String url) {
        Bundle args = new Bundle();
        ArticleFragment fragment = new ArticleFragment();
        args.putString(KEY_URL, url);
        fragment.setArguments(args);
        EventHelperKt.sendEvent(EVENT_OPEN_ARTICLE);
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_article;
    }

    @Override
    public ArticleViewModel getViewModel() {
        return ViewModelProviders.of(this, factory).get(ArticleViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        getViewModel().setNavigator(this);
        Bundle args = getArguments();
        if (args != null) {
            articleUrl = args.getString(KEY_URL);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);;
        setUp();
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(getViewDataBinding().articleToolbar);
        getViewModel().setUrl(articleUrl);
        getViewModel().fetchArticlePost(FETCH_INIT);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    private void setUp() {
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        RecyclerView rv = getViewDataBinding().postRecyclerView;
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(new ArticleAdapter(getViewModel()));
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
                LottieAnimationView backToTop = getViewDataBinding().backToTop;
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (firstVisibleItemPosition == 0) {
                        backToTop.setVisibility(View.GONE);
                        backToTop.pauseAnimation();
                    } else {
                        backToTop.setVisibility(View.VISIBLE);
                    }
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    backToTop.setVisibility(View.GONE);
                    backToTop.pauseAnimation();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.article, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_in_webview:
                WebViewActivity.startWebViewWith(SERVER_BASE_URL + articleUrl, getContext());
                break;
            case R.id.action_extract_urls:
                ArrayList<String> linkList = getViewModel().extractDownloadUrl();
                if (linkList != null && !linkList.isEmpty()) {
                    DownloadLinkDialog.newInstance(linkList).show(getFragmentManager());
                } else {
                    Toast.makeText(getContext(), R.string.download_link_not_found, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_add_to_my_favorite:
                getViewModel().addToFavorite();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleError(Throwable t) {
        Timber.e(t);
    }

    @Override
    public void showMessage(@StringRes int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void scrollToTop() {
        getViewDataBinding().postRecyclerView.smoothScrollToPosition(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReplyPostEvent event) {
        ReplyPostDialog.newInstance(event.getReplyUrl()).show(getChildFragmentManager());
    }
}
