package top.easelink.lcg.ui.main.article.view;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import top.easelink.framework.BR;
import top.easelink.framework.base.BaseFragment;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.FragmentArticleBinding;
import top.easelink.lcg.ui.ViewModelProviderFactory;
import top.easelink.lcg.ui.main.article.viewmodel.ArticleViewModel;
import top.easelink.lcg.ui.main.articles.view.ArticlesAdapter;
import top.easelink.lcg.ui.main.model.Article;

import javax.inject.Inject;

public class ArticleFragment extends BaseFragment<FragmentArticleBinding, ArticleViewModel> implements ArticleNavigator {

    @Inject
    ViewModelProviderFactory factory;
    public static final String TAG = ArticleFragment.class.getSimpleName();
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
        mArticleViewModel.fetchArticle(articleUrl);
    }

    private void setUp() {
        mArticleAdapter = new ArticleAdapter();
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mFragmentArticleBinding.postRecyclerView.setLayoutManager(mLayoutManager);
        mFragmentArticleBinding.postRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mFragmentArticleBinding.postRecyclerView.setAdapter(mArticleAdapter);
    }

    @Override
    public void handleError(Throwable t) {

    }

}
