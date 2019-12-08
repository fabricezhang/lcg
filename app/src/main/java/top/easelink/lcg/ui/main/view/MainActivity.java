package top.easelink.lcg.ui.main.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import pub.devrel.easypermissions.EasyPermissions;
import top.easelink.framework.base.BaseActivity;
import top.easelink.framework.base.BaseFragment;
import top.easelink.lcg.BR;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.ActivityMainBinding;
import top.easelink.lcg.databinding.NavHeaderMainBinding;
import top.easelink.lcg.mta.EventHelperKt;
import top.easelink.lcg.ui.main.about.view.AboutFragment;
import top.easelink.lcg.ui.main.article.view.ArticleFragment;
import top.easelink.lcg.ui.main.articles.view.ArticlesFragment;
import top.easelink.lcg.ui.main.articles.view.FavoriteArticlesFragment;
import top.easelink.lcg.ui.main.articles.view.ForumArticlesFragment;
import top.easelink.lcg.ui.main.forumnav.view.ForumNavigationFragment;
import top.easelink.lcg.ui.main.me.view.MeFragment;
import top.easelink.lcg.ui.main.model.NewMessageEvent;
import top.easelink.lcg.ui.main.model.NotificationInfo;
import top.easelink.lcg.ui.main.model.OpenArticleEvent;
import top.easelink.lcg.ui.main.model.OpenForumEvent;
import top.easelink.lcg.ui.main.model.OpenNotificationsPageEvent;
import top.easelink.lcg.ui.main.model.TabModel;
import top.easelink.lcg.ui.main.viewmodel.MainViewModel;
import top.easelink.lcg.ui.search.view.SearchActivity;
import top.easelink.lcg.utils.ActivityUtils;
import top.easelink.lcg.utils.ToastUtilsKt;

import static top.easelink.lcg.mta.MTAConstantKt.EVENT_OPEN_FORUM;
import static top.easelink.lcg.mta.MTAConstantKt.PROP_FORUM_NAME;
import static top.easelink.lcg.utils.ActivityUtils.TAG_PREFIX;
import static top.easelink.lcg.utils.WebsiteConstant.APP_RELEASE_PAGE;
import static top.easelink.lcg.utils.WebsiteConstant.SEARCH_URL;
import static top.easelink.lcg.utils.WebsiteConstant.URL_KEY;

public class MainActivity
        extends BaseActivity<ActivityMainBinding, MainViewModel>
        implements HasSupportFragmentInjector,
        BottomNavigationView.OnNavigationItemSelectedListener,
        EasyPermissions.PermissionCallbacks {

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;
    private static WeakReference<MainActivity> mainActivityWeakReference;
    private DrawerLayout mDrawer;
    private Long lastBackPressed = 0L;

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
        return ViewModelProviders.of(this).get(MainViewModel.class);
    }

    @Override
    public void onBackPressed() {
        if (!mFragmentTags.empty()) {
            String tag = mFragmentTags.peek();
            if (onFragmentDetached(tag)) {
                mFragmentTags.pop();
                syncBottomViewNavItemState();
                return;
            } else {
                // check viewpager
                ViewPager viewPager = getViewDataBinding().mainViewPager;
                if (viewPager.isShown() && viewPager.getCurrentItem() != 0) {
                    viewPager.setCurrentItem(0, true);
                    return;
                }
            }
        }
        if (System.currentTimeMillis() - lastBackPressed > 2000) {
            Toast.makeText(this, R.string.app_exit_tip, Toast.LENGTH_SHORT).show();
            lastBackPressed = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mainActivityWeakReference = new WeakReference<>(this);
        setUp();
        requestPermissions();
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

    /**
     * manage the bottom navigation view item selected state
     * depends on current top fragment
     */
    private void syncBottomViewNavItemState() {
        BottomNavigationView view = getViewDataBinding().bottomNavigation;
        try {
            String topFragment = mFragmentTags.peek();
            view.setOnNavigationItemSelectedListener(null);
            if (topFragment.startsWith("android")) {
                // means that current top fragment is the tab-layout
                view.setSelectedItemId(R.id.action_home);
                unlockDrawer();
            } else if (topFragment.endsWith(FavoriteArticlesFragment.class.getSimpleName())) {
                view.setSelectedItemId(R.id.action_favorite);
            } else if (topFragment.endsWith(ForumNavigationFragment.class.getSimpleName())) {
                view.setSelectedItemId(R.id.action_forum_navigation);
            }
        } catch (EmptyStackException ese) {
            view.setSelectedItemId(R.id.action_home);
        } finally {
            view.setOnNavigationItemSelectedListener(this);
        }
    }

    private void setUp() {
        mDrawer = getViewDataBinding().drawerView;
        Toolbar mToolbar = getViewDataBinding().toolbar;
        TabLayout mTabLayout = getViewDataBinding().mainTab;
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
        setupDrawerNavMenu();
        setupBottomNavMenu();

        getViewDataBinding().mainViewPager.setAdapter(new MainViewPagerAdapter(
                getSupportFragmentManager(), MainActivity.this));
        mTabLayout.setupWithViewPager(getViewDataBinding().mainViewPager);
    }

    private void setupBottomNavMenu() {
        getViewDataBinding().bottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    private void setupDrawerNavMenu() {
        NavHeaderMainBinding navHeaderMainBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.nav_header_main, getViewDataBinding().navigationView, false);
        getViewDataBinding().navigationView.addHeaderView(navHeaderMainBinding.getRoot());
        navHeaderMainBinding.setViewModel(getViewModel());

        getViewDataBinding().navigationView.setNavigationItemSelectedListener(
                item -> {
                    mDrawer.closeDrawer(GravityCompat.START);
                    switch (item.getItemId()) {
                        case R.id.navItemAbout:
                            showFragment(AboutFragment.newInstance());
                            return true;
                        case R.id.navItemRelease:
                            showFragment(ArticleFragment.newInstance(APP_RELEASE_PAGE));
                            return true;
                        default:
                            return false;
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenArticleEvent event) {
        showFragment(ArticleFragment.newInstance(event.getUrl()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenForumEvent event) {
        Properties prop = new Properties();
        prop.setProperty(PROP_FORUM_NAME, event.getTitle());
        EventHelperKt.sendKVEvent(EVENT_OPEN_FORUM, prop);
        showFragment(ForumArticlesFragment.newInstance(event.getTitle(), event.getUrl()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenNotificationsPageEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewMessageEvent event) {
        NotificationInfo info = event.getNotificationInfo();
        if (info.isNotEmpty()) {
            ToastUtilsKt.showMessage(getString(R.string.notification_arrival));
        }
    }

    private void showFragment(BaseFragment fragment) {
        ActivityUtils.addFragmentInActivity(getSupportFragmentManager(),
                fragment,
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
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (getViewDataBinding().bottomNavigation.getSelectedItemId() == menuItem.getItemId()) {
            return false;
        }
        while (!mFragmentTags.isEmpty()
                && mFragmentTags.peek().startsWith(TAG_PREFIX)) {
            onFragmentDetached(mFragmentTags.pop());
        }
        switch (menuItem.getItemId()) {
            case R.id.action_favorite:
                showFragment(FavoriteArticlesFragment.newInstance());
                break;
            case R.id.action_forum_navigation:
                showFragment(ForumNavigationFragment.newInstance());
                break;
            case R.id.action_about_me:
                showFragment(MeFragment.newInstance());
                break;
            case R.id.action_home:
            default:
                break;
        }
        return true;
    }

    private static final int RW_EXTERNAL_STORAGE_CODE = 1111;
    private void requestPermissions() {
        String[] perms = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_request), RW_EXTERNAL_STORAGE_CODE, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        ToastUtilsKt.showMessage(R.string.permission_grant);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == RW_EXTERNAL_STORAGE_CODE) {
            ToastUtilsKt.showMessage(R.string.permission_denied);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public static class MainViewPagerAdapter extends FragmentPagerAdapter {

        private List<TabModel> tabModels;

        MainViewPagerAdapter(FragmentManager fm, Context context) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            tabModels = new ArrayList<>();
            tabModels.add(new TabModel(context.getString(R.string.tab_title_hot), "hot"));
            tabModels.add(new TabModel(context.getString(R.string.tab_title_tech), "tech"));
            tabModels.add(new TabModel(context.getString(R.string.tab_title_digest), "digest"));
            tabModels.add(new TabModel(context.getString(R.string.tab_title_new_thread), "newthread"));
        }

        @Override
        @NonNull
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
