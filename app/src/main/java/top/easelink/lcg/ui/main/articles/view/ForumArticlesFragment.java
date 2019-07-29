package top.easelink.lcg.ui.main.articles.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
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
import top.easelink.lcg.databinding.FragmentForumArticlesBinding;
import top.easelink.lcg.ui.ViewModelProviderFactory;
import top.easelink.lcg.ui.main.articles.viewmodel.ArticlesAdapter;
import top.easelink.lcg.ui.main.articles.viewmodel.ForumArticlesViewModel;

import javax.inject.Inject;

import static top.easelink.lcg.ui.main.articles.viewmodel.ArticlesViewModel.FETCH_INIT;

public class ForumArticlesFragment extends BaseFragment<FragmentForumArticlesBinding, ForumArticlesViewModel>
        implements ArticlesNavigator {

    private static final String ARG_PARAM = "url";
    private static final String ARG_TITLE = "title";

    @Inject
    ViewModelProviderFactory factory;
    private FragmentForumArticlesBinding mFragmentArticlesBinding;
    private ForumArticlesViewModel mArticlesViewModel;

    public static ForumArticlesFragment newInstance(String title, String param) {
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        args.putString(ARG_TITLE, title);
        ForumArticlesFragment fragment = new ForumArticlesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_forum_articles;
    }

    @Override
    public ForumArticlesViewModel getViewModel() {
        mArticlesViewModel = ViewModelProviders.of(this, factory).get(ForumArticlesViewModel.class);
        return mArticlesViewModel;
    }

    @Override
    public void handleError(Throwable t) {
        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void scrollToTop() {
        mFragmentArticlesBinding.recyclerView.smoothScrollToPosition(0);
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
    }

    private void setUp() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mArticlesViewModel.initUrl(getArguments().getString(ARG_PARAM));
            mArticlesViewModel.setTitle(getArguments().getString(ARG_TITLE));
        }

        ArticlesAdapter mArticlesAdapter = new ArticlesAdapter(mArticlesViewModel);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
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
        swipeRefreshLayout.setOnRefreshListener(() -> mArticlesViewModel.fetchArticles(FETCH_INIT));
    }

    @Override
    public void showMessage(@StringRes int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
    }
}
