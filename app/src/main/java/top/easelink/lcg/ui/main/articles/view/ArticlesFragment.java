package top.easelink.lcg.ui.main.articles.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import top.easelink.framework.BR;
import top.easelink.framework.base.BaseFragment;
import top.easelink.framework.customview.ScrollChildSwipeRefreshLayout;
import top.easelink.lcg.LCGApp;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.FragmentArticlesBinding;
import top.easelink.lcg.ui.ViewModelProviderFactory;
import top.easelink.lcg.ui.main.articles.viewmodel.ArticlesViewModel;

import javax.inject.Inject;

import static top.easelink.lcg.ui.main.articles.viewmodel.ArticlesViewModel.FETCH_INIT;

public class ArticlesFragment extends BaseFragment<FragmentArticlesBinding, ArticlesViewModel>
        implements ArticlesNavigator, ArticlesAdapter.ArticleAdapterListener {

    public static String TAG = ArticlesFragment.class.getSimpleName();
    private static final String ARG_PARAM = "param";

    @Inject
    ViewModelProviderFactory factory;
    private LinearLayoutManager mLayoutManager;
    private ArticlesAdapter mArticlesAdapter;
    private FragmentArticlesBinding mFragmentArticlesBinding;
    private ArticlesViewModel mArticlesViewModel;
    private String mParam;

    public static ArticlesFragment newInstance(String param) {
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        ArticlesFragment fragment = new ArticlesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_articles;
    }

    @Override
    public ArticlesViewModel getViewModel() {
        mArticlesViewModel = ViewModelProviders.of(this, factory).get(ArticlesViewModel.class);
        return mArticlesViewModel;
    }

    @Override
    public void handleError(Throwable t) {
        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArticlesViewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentArticlesBinding = getViewDataBinding();
        setUp();
        fetchArticles(FETCH_INIT);
    }

    private void setUp() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mParam = getArguments().getString(ARG_PARAM);
        }
        mArticlesAdapter = new ArticlesAdapter(this);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mFragmentArticlesBinding.recyclerView.setLayoutManager(mLayoutManager);
        mFragmentArticlesBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mFragmentArticlesBinding.recyclerView.setAdapter(mArticlesAdapter);
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = getViewDataBinding().refreshLayout;
        Context context = LCGApp.getContext();
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(context, R.color.colorPrimary),
                ContextCompat.getColor(context, R.color.colorAccent),
                ContextCompat.getColor(context, R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(mFragmentArticlesBinding.recyclerView);
        swipeRefreshLayout.setOnRefreshListener(() -> mArticlesViewModel.fetchArticles(mParam, FETCH_INIT));
    }

    @Override
    public void fetchArticles(int type) {
        mArticlesViewModel.fetchArticles(mParam, type);
    }
}
