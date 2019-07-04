package top.easelink.lcg.ui.home.view;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import top.easelink.framework.BR;
import top.easelink.framework.base.BaseFragment;
import top.easelink.framework.customview.ScrollChildSwipeRefreshLayout;
import top.easelink.framework.utils.CommonUtils;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.FragmentHomeBinding;
import top.easelink.lcg.ui.home.viewmodel.HomeViewModel;

import javax.inject.Inject;

public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeViewModel>
        implements HomeNavigator, ArticleAdapter.ArticleAdapterListener {

    public static final String TAG = HomeFragment.class.getSimpleName();

    @Inject
    ArticleAdapter articleAdapter;
    @Inject
    LinearLayoutManager mLayoutManager;
    private FragmentHomeBinding mFragmentHomeBinding;
    private HomeViewModel mHomeViewModel;

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
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
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        mHomeViewModel.fetchArticles();
    }

    private void setUp() {

        mLayoutManager .setOrientation(RecyclerView.VERTICAL);
        mFragmentHomeBinding.recyclerView.setLayoutManager(mLayoutManager);
        mFragmentHomeBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mFragmentHomeBinding.recyclerView.setAdapter(articleAdapter);
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = getViewDataBinding().refreshLayout;
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(mFragmentHomeBinding.recyclerView);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHomeViewModel.fetchArticles();
            }
        });
    }

    @Override
    public void onRetryClick() {
        mHomeViewModel.fetchArticles();
    }
}
