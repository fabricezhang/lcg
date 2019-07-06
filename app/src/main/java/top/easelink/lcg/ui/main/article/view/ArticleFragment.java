package top.easelink.lcg.ui.main.article.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import top.easelink.framework.BR;
import top.easelink.framework.base.BaseFragment;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.FragmentArticleBinding;
import top.easelink.lcg.ui.ViewModelProviderFactory;
import top.easelink.lcg.ui.main.article.viewmodel.ArticleViewModel;
import top.easelink.lcg.ui.main.model.Article;

import javax.inject.Inject;

public class ArticleFragment extends BaseFragment<FragmentArticleBinding, ArticleViewModel> implements ArticleNavigator {

    @Inject
    ViewModelProviderFactory factory;
    public static final String TAG = ArticleFragment.class.getSimpleName();
    private static final String KEY_URL = "KEY_URL";

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
    }
}
