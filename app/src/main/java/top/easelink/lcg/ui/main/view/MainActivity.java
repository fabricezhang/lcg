package top.easelink.lcg.ui.main.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
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
import timber.log.Timber;
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
import top.easelink.lcg.ui.main.model.OpenArticleEvent;
import top.easelink.lcg.ui.main.model.TabModel;
import top.easelink.lcg.ui.main.model.Article;
import top.easelink.lcg.ui.main.viewmodel.MainViewModel;
import top.easelink.lcg.utils.ActivityUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

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
        // check viewpager
        if (mViewPager.isShown() && mViewPager.getCurrentItem() != 0) {
            mViewPager.setCurrentItem(0, true);
            return;
        }
        if(onFragmentDetached(ArticleFragment.TAG)) return;
        if(onFragmentDetached(AboutFragment.TAG)) return;
        super.onBackPressed();
    }

    public boolean onFragmentDetached(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
                    .remove(fragment)
                    .commit();
            unlockDrawer();
            return true;
        }
        return false;
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
                getSupportFragmentManager(), getBaseContext()));
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
                        case R.id.navItemLogout:
                            mMainViewModel.logout();
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
        lockDrawer();
        ActivityUtils.addFragmentInActivity(getSupportFragmentManager(),
                AboutFragment.newInstance(),
                R.id.clRootView, AboutFragment.TAG);
    }

    private void showArticleFragment(Article article) {
        ActivityUtils.addFragmentInActivity(getSupportFragmentManager(),
                ArticleFragment.newInstance(article),
                R.id.clRootView, ArticleFragment.TAG);
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
