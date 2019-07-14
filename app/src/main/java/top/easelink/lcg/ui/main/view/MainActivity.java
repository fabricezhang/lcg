package top.easelink.lcg.ui.main.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import top.easelink.framework.BR;
import top.easelink.framework.base.BaseActivity;
import top.easelink.lcg.BuildConfig;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.ActivityMainBinding;
import top.easelink.lcg.databinding.NavHeaderMainBinding;
import top.easelink.lcg.ui.ViewModelProviderFactory;
import top.easelink.lcg.ui.about.view.AboutFragment;
import top.easelink.lcg.ui.main.article.view.ArticleFragment;
import top.easelink.lcg.ui.main.articles.view.ArticlesFragment;
import top.easelink.lcg.ui.main.articles.view.ForumArticlesFragment;
import top.easelink.lcg.ui.main.model.Article;
import top.easelink.lcg.ui.main.model.OpenArticleEvent;
import top.easelink.lcg.ui.main.model.dto.TabModel;
import top.easelink.lcg.ui.main.viewmodel.MainViewModel;
import top.easelink.lcg.ui.webview.view.WebViewActivity;
import top.easelink.lcg.ui.search.view.SearchActivity;
import top.easelink.lcg.utils.ActivityUtils;

import javax.inject.Inject;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static top.easelink.lcg.ui.main.source.remote.RxArticleService.SERVER_BASE_URL;
import static top.easelink.lcg.utils.WebsiteConstant.URL_KEY;
import static top.easelink.lcg.utils.ActivityUtils.TAG_PREFIX;
import static top.easelink.lcg.utils.WebsiteConstant.*;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> implements MainNavigator, HasSupportFragmentInjector {

    @Inject
    ViewModelProviderFactory factory;
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    private ActivityMainBinding mActivityMainBinding;
    private DrawerLayout mDrawer;
    private MainViewModel mMainViewModel;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private static WeakReference<MainActivity> mainActivityWeakReference;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public MainViewModel getViewModel() {
        mMainViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
        return mMainViewModel;
    }

    @Override
    public void handleError(Throwable throwable) {
        // handle error
    }

    @Override
    public void onBackPressed() {
        if (!mFragmentTags.empty()) {
            String tag = mFragmentTags.pop();
            if (onFragmentDetached(tag)) {
                return;
            } else {
                // check viewpager
                if (mViewPager.isShown() && mViewPager.getCurrentItem() != 0) {
                    mViewPager.setCurrentItem(0, true);
                    return;
                }
            }
        }
        super.onBackPressed();
    }

    @Override
    public void openLoginActivity() { }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mActivityMainBinding = getViewDataBinding();
        mMainViewModel.setNavigator(this);
        mainActivityWeakReference = new WeakReference<>(this);
        setUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDrawer != null) {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void lockDrawer() {
        if (mDrawer != null) {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    private void setUp() {
        mDrawer = mActivityMainBinding.drawerView;
        mToolbar = mActivityMainBinding.toolbar;
        mNavigationView = mActivityMainBinding.navigationView;
        mViewPager = mActivityMainBinding.mainViewPager;
        mTabLayout = mActivityMainBinding.mainTab;
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawer,
                mToolbar,
                R.string.open_drawer,
                R.string.close_drawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideKeyboard();
            }
        };
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        setupNavMenu();

        String version = getString(R.string.version) + " " + BuildConfig.VERSION_NAME;
        mMainViewModel.updateAppVersion(version);
        mMainViewModel.onNavMenuCreated();

        mViewPager.setAdapter(new MainViewPagerAdapter(
                getSupportFragmentManager(), MainActivity.this));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setupNavMenu() {
        NavHeaderMainBinding navHeaderMainBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.nav_header_main, mActivityMainBinding.navigationView, false);
        mActivityMainBinding.navigationView.addHeaderView(navHeaderMainBinding.getRoot());
        navHeaderMainBinding.setViewModel(mMainViewModel);

        mNavigationView.setNavigationItemSelectedListener(
                item -> {
                    mDrawer.closeDrawer(GravityCompat.START);
                    switch (item.getItemId()) {
                        case R.id.navItemAbout:
                            showAboutFragment();
                            return true;
                        case R.id.navItemSoftware:
                            showForumFragment(SOFTWARE_TITLE, SOFTWARE_URL);
                            return true;
                        case R.id.navItemFreeChat:
                            WebViewActivity.startWebViewWith(SERVER_BASE_URL + FREE_CHAT_URL, MainActivity.this);
                            return true;
                        case R.id.navItemMobileSecurity:
                            showForumFragment(MOB_SECURITY_TITLE, MOB_SECURITY_URL);
                            return true;
                        case R.id.navItemOrigin:
                            showForumFragment(ORIGINAL_TITLE, ORIGINAL_URL);
                            return true;
                        default:
                            return false;
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenArticleEvent event) {
        showArticleFragment(event.getArticle());
    }

    private void showAboutFragment() {
        ActivityUtils.addFragmentInActivity(getSupportFragmentManager(),
                AboutFragment.newInstance(),
                R.id.clRootView);
    }

    private void showArticleFragment(Article article) {
        ActivityUtils.addFragmentInActivity(getSupportFragmentManager(),
                ArticleFragment.newInstance(article),
                R.id.clRootView);
    }

    private void showForumFragment(String title, String forumUrl) {
        ActivityUtils.addFragmentInActivity(getSupportFragmentManager(),
                ForumArticlesFragment.newInstance(title, forumUrl),
                R.id.clRootView);
    }

    @Override
    public boolean onFragmentDetached(String tag) {
        if (tag != null && tag.startsWith(TAG_PREFIX)) {
            return super.onFragmentDetached(tag);
        } else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        // Get the SearchView and set the searchable configuration
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSubmitButtonEnabled(true);
//        // Assumes current activity is the searchable activity
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                hideKeyboard();
                Context context = mainActivityWeakReference.get();
                if (context != null) {
                    Intent intent = new Intent(context, SearchActivity.class);
                    intent.putExtra(URL_KEY, String.format(SEARCH_URL, query));
                    context.startActivity(intent);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void unlockDrawer() {
        if (mDrawer != null) {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    public static class MainViewPagerAdapter extends FragmentPagerAdapter {

        private List<TabModel> tabModels;

        MainViewPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            tabModels = new ArrayList<>();
            tabModels.add(new TabModel(context.getString(R.string.tab_title_hot), "hot"));
            tabModels.add(new TabModel(context.getString(R.string.tab_title_tech), "tech"));
            tabModels.add(new TabModel(context.getString(R.string.tab_title_digest), "digest"));
            tabModels.add(new TabModel(context.getString(R.string.tab_title_new_thread), "newthread"));
        }

        @Override
        public Fragment getItem(int position) {
            return ArticlesFragment.newInstance(tabModels.get(position).getUrl());
        }

        @Override
        public int getCount() {
            return tabModels.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabModels.get(position).getTitle();
        }
    }

}
