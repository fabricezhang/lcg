package top.easelink.lcg.ui.main.article.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import timber.log.Timber;
import top.easelink.framework.BR;
import top.easelink.framework.base.BaseFragment;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.FragmentArticleBinding;
import top.easelink.lcg.ui.ViewModelProviderFactory;
import top.easelink.lcg.ui.main.article.viewmodel.ArticleAdapter;
import top.easelink.lcg.ui.main.article.viewmodel.ArticleViewModel;
import top.easelink.lcg.ui.main.source.model.Article;
import top.easelink.lcg.ui.webview.view.WebViewActivity;

import javax.inject.Inject;
import java.util.ArrayList;

import static top.easelink.lcg.ui.main.article.viewmodel.ArticleViewModel.FETCH_INIT;
import static top.easelink.lcg.ui.main.source.remote.ArticlesRemoteDataSource.SERVER_BASE_URL;


public class ArticleFragment extends BaseFragment<FragmentArticleBinding, ArticleViewModel>
        implements ArticleNavigator {

    @Inject
    ViewModelProviderFactory factory;
    private static final String KEY_URL = "KEY_URL";
    private LinearLayoutManager mLayoutManager;
    private ArticleAdapter mArticleAdapter;
    private FragmentArticleBinding mFragmentArticleBinding;

    private String articleUrl;

    private ArticleViewModel mArticleViewModel;

    public static ArticleFragment newInstance(Article article) {
        Bundle args = new Bundle();
        ArticleFragment fragment = new ArticleFragment();
        args.putString(KEY_URL, article.getUrl());
        fragment.setArguments(args);
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
        mArticleViewModel = ViewModelProviders.of(this, factory).get(ArticleViewModel.class);
        mArticleViewModel.setUrl(articleUrl);
        return mArticleViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArticleViewModel.setNavigator(this);
        Bundle args = getArguments();
        if (args != null) {
            articleUrl = args.getString(KEY_URL);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentArticleBinding = getViewDataBinding();
        setUp();
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mFragmentArticleBinding.articleToolbar);
        mArticleViewModel.setUrl(articleUrl);
        mArticleViewModel.fetchArticlePost(FETCH_INIT);
    }

    private void setUp() {
        mArticleAdapter = new ArticleAdapter(mArticleViewModel);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        RecyclerView rv = mFragmentArticleBinding.postRecyclerView;
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(mArticleAdapter);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
                LottieAnimationView backToTop = mFragmentArticleBinding.backToTop;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.article, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_in_webview:
                WebViewActivity.startWebViewWith(SERVER_BASE_URL + articleUrl, getContext());
                break;
            case R.id.action_extract_urls:
                ArrayList<String> linkList = mArticleViewModel.extractDownloadUrl();
                if (linkList != null && !linkList.isEmpty()) {
                    DownloadLinkDialog.newInstance(linkList).show(getFragmentManager());
                } else {
                    Toast.makeText(getContext(), R.string.download_link_not_found, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_add_to_my_favorite:
                mArticleViewModel.addToFavorite();
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
    public void onAddToFavoriteFinished(boolean res) {
        Toast.makeText(getContext(),
                res?R.string.add_to_favorite_successfully:R.string.add_to_favorite_failed,
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void scrollToTop() {
        mFragmentArticleBinding.postRecyclerView.smoothScrollToPosition(0);
    }
}
