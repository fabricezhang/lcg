package top.easelink.lcg.ui.search.view;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import top.easelink.framework.BR;
import top.easelink.framework.base.BaseActivity;
import top.easelink.framework.customview.ScrollChildSwipeRefreshLayout;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.ActivitySearchBinding;
import top.easelink.lcg.ui.ViewModelProviderFactory;
import top.easelink.lcg.ui.search.model.OpenSearchResultEvent;
import top.easelink.lcg.ui.search.viewmodel.SearchResultAdapter;
import top.easelink.lcg.ui.search.viewmodel.SearchViewModel;
import top.easelink.lcg.ui.webview.view.WebViewActivity;

import javax.inject.Inject;

import static top.easelink.lcg.ui.search.viewmodel.SearchResultAdapter.SearchAdapterListener.FETCH_INIT;
import static top.easelink.lcg.utils.WebsiteConstant.URL_KEY;

public class SearchActivity extends BaseActivity<ActivitySearchBinding, SearchViewModel>
        implements SearchNavigator {

    @Inject
    ViewModelProviderFactory factory;

    private SearchViewModel mSearchViewModel;
    private ActivitySearchBinding mSearchActivityBinding;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    public SearchViewModel getViewModel() {
        mSearchViewModel = ViewModelProviders.of(this, factory).get(SearchViewModel.class);
        return mSearchViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mSearchActivityBinding = getViewDataBinding();
        setupRecyclerView();
        mSearchViewModel.setNavigator(this);
        mSearchViewModel.initUrl(getIntent().getStringExtra(URL_KEY));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void setupRecyclerView(){
        SearchResultAdapter searchResultAdapter = new SearchResultAdapter(mSearchViewModel);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(SearchActivity.this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        RecyclerView rv = mSearchActivityBinding.recyclerView;
        mSearchActivityBinding.setLifecycleOwner(this);

        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(searchResultAdapter);

        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = getViewDataBinding().refreshLayout;
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(mSearchActivityBinding.recyclerView);
        swipeRefreshLayout.setOnRefreshListener(() -> mSearchViewModel.doSearchQuery(FETCH_INIT));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenSearchResultEvent event) {
        WebViewActivity.startWebViewWith(event.getSearchResult().getUrl(), this);
    }

    @Override
    public void handleError(Throwable throwable) {
        Toast.makeText(SearchActivity.this,
                getString(R.string.error),
                Toast.LENGTH_SHORT)
                .show();
    }
}
