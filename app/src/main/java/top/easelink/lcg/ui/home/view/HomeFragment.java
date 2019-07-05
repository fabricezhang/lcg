package top.easelink.lcg.ui.home.view;

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
import top.easelink.lcg.databinding.FragmentHomeBinding;
import top.easelink.lcg.ui.ViewModelProviderFactory;
import top.easelink.lcg.ui.home.viewmodel.HomeViewModel;

import javax.inject.Inject;

import static top.easelink.lcg.ui.home.viewmodel.HomeViewModel.FETCH_INIT;

public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeViewModel>
        implements HomeNavigator, ArticleAdapter.ArticleAdapterListener {

    public static String TAG = HomeFragment.class.getSimpleName();
    private static final String ARG_PARAM = "param";

    @Inject
    ViewModelProviderFactory factory;
    private LinearLayoutManager mLayoutManager;
    private ArticleAdapter mArticleAdapter;
    private FragmentHomeBinding mFragmentHomeBinding;
    private HomeViewModel mHomeViewModel;
    private String mParam;

    public static HomeFragment newInstance(String param) {
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public HomeViewModel getViewModel() {
        mHomeViewModel = ViewModelProviders.of(this, factory).get(HomeViewModel.class);
        return mHomeViewModel;
    }

    @Override
    public void goBack() {
        getBaseActivity().onFragmentDetached(TAG);
    }

    @Override
    public void handleError(Throwable t) {
        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeViewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentHomeBinding = getViewDataBinding();
        setUp();
        fetchArticles(FETCH_INIT);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setUp() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mParam = getArguments().getString(ARG_PARAM);
        }
        mArticleAdapter = new ArticleAdapter(this, mParam);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mFragmentHomeBinding.recyclerView.setLayoutManager(mLayoutManager);
        mFragmentHomeBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mFragmentHomeBinding.recyclerView.setAdapter(mArticleAdapter);
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = getViewDataBinding().refreshLayout;
        Context context = LCGApp.getContext();
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(context, R.color.colorPrimary),
                ContextCompat.getColor(context, R.color.colorAccent),
                ContextCompat.getColor(context, R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(mFragmentHomeBinding.recyclerView);
        swipeRefreshLayout.setOnRefreshListener(() -> mHomeViewModel.fetchArticles(mParam, FETCH_INIT));
    }

    @Override
    public void fetchArticles(int type) {
        mHomeViewModel.fetchArticles(mParam, type);
    }
}
