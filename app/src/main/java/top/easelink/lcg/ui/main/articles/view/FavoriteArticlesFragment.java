package top.easelink.lcg.ui.main.articles.view;

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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import top.easelink.framework.BR;
import top.easelink.framework.base.BaseFragment;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.FragmentFavoriteArticlesBinding;
import top.easelink.lcg.ui.ViewModelProviderFactory;
import top.easelink.lcg.ui.main.articles.viewmodel.FavoriteArticlesAdapter;
import top.easelink.lcg.ui.main.articles.viewmodel.FavoriteArticlesViewModel;

import javax.inject.Inject;

import static top.easelink.lcg.ui.main.articles.viewmodel.FavoriteArticlesViewModel.FETCH_INIT;

public class FavoriteArticlesFragment extends BaseFragment<FragmentFavoriteArticlesBinding, FavoriteArticlesViewModel>
        implements ArticlesNavigator {

    @Inject
    ViewModelProviderFactory factory;
    private FragmentFavoriteArticlesBinding mFragmentArticlesBinding;
    private FavoriteArticlesViewModel mArticlesViewModel;

    public static FavoriteArticlesFragment newInstance() {
        return new FavoriteArticlesFragment();
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_favorite_articles;
    }

    @Override
    public FavoriteArticlesViewModel getViewModel() {
        mArticlesViewModel = ViewModelProviders.of(this, factory).get(FavoriteArticlesViewModel.class);
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
        setUpRecyclerView();
        setupMenu();
        mArticlesViewModel.fetchArticles(FETCH_INIT);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.favorite_articles, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_remove_all:
                mArticlesViewModel.removeAllFavorites();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showMessage(@StringRes int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
    }

    private void setUpRecyclerView() {
        FavoriteArticlesAdapter mArticlesAdapter = new FavoriteArticlesAdapter(mArticlesViewModel);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mFragmentArticlesBinding.recyclerView.setLayoutManager(mLayoutManager);
        mFragmentArticlesBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mFragmentArticlesBinding.recyclerView.setAdapter(mArticlesAdapter);
    }

    private void setupMenu() {
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mFragmentArticlesBinding.articleToolbar);
    }
}
