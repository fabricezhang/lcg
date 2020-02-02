package top.easelink.lcg.ui.main.view

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import top.easelink.framework.topbase.TopActivity
import top.easelink.framework.utils.addFragmentInActivity
import top.easelink.framework.utils.popBackFragmentUntil
import top.easelink.lcg.R
import top.easelink.lcg.mta.*
import top.easelink.lcg.ui.main.article.view.ArticleFragment
import top.easelink.lcg.ui.main.articles.view.ForumArticlesFragment.Companion.newInstance
import top.easelink.lcg.ui.main.forumnav3.view.ForumNavigationV3Fragment
import top.easelink.lcg.ui.main.largeimg.view.LargeImageDialog
import top.easelink.lcg.ui.main.me.view.MeFragment
import top.easelink.lcg.ui.main.message.view.MessageFragment
import top.easelink.lcg.ui.main.model.NewMessageEvent
import top.easelink.lcg.ui.main.model.OpenArticleEvent
import top.easelink.lcg.ui.main.model.OpenForumEvent
import top.easelink.lcg.ui.main.model.OpenLargeImageViewEvent
import top.easelink.lcg.ui.main.recommand.view.RecommendFragment
import top.easelink.lcg.utils.showMessage
import java.util.*
import kotlin.system.exitProcess

class MainActivity : TopActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private var lastBackPressed = 0L

    private var bubbleView: View? = null

    override fun onBackPressed() {
        if (mFragmentTags.isNotEmpty() && mFragmentTags.size > 1) {
            while (onFragmentDetached(mFragmentTags.pop())) {
                syncBottomViewNavItemState()
                return
            }
        }
        if (System.currentTimeMillis() - lastBackPressed > 2000) {
            Toast.makeText(this, R.string.app_exit_tip, Toast.LENGTH_SHORT).show()
            lastBackPressed = System.currentTimeMillis()
        } else {
            finish()
            exitProcess(0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)
        setupBottomNavMenu()
        showFragment(RecommendFragment::class.java)
        checkPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun setupBottomNavMenu() {
        bottom_navigation.setOnNavigationItemSelectedListener(this)
    }

    /**
     * manage the bottom navigation view item selected state
     * depends on current top fragment
     */
    private fun syncBottomViewNavItemState() {
        val view: BottomNavigationView = bottom_navigation
        try {
            view.setOnNavigationItemSelectedListener(null)
            when (mFragmentTags.peek()) {
                RecommendFragment::class.java.simpleName -> {
                    view.selectedItemId = R.id.action_home
                }
                MessageFragment::class.java.simpleName -> {
                    view.selectedItemId = R.id.action_message
                }
                ForumNavigationV3Fragment::class.java.simpleName -> {
                    view.selectedItemId = R.id.action_forum_navigation
                }
                MeFragment::class.java.simpleName -> {
                    view.selectedItemId = R.id.action_about_me
                }
            }
        } catch (ese: EmptyStackException) {
            view.selectedItemId = R.id.action_home
        } finally {
            view.setOnNavigationItemSelectedListener(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: OpenArticleEvent) {
        sendEvent(EVENT_OPEN_ARTICLE)
        showFragment(ArticleFragment(event.url))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: OpenForumEvent) {
        val prop = Properties()
        prop.setProperty(PROP_FORUM_NAME, event.title)
        sendKVEvent(EVENT_OPEN_FORUM, prop)
        showFragment(newInstance(event.title, event.url, event.showTab))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: NewMessageEvent) {
        val info = event.notificationInfo
        if (info.isNotEmpty()) {
            showMessage(getString(R.string.notification_arrival))
            showBubbleView(2)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: OpenLargeImageViewEvent) {
        if (event.url.isNotEmpty()) {
            LargeImageDialog(event.url).show(supportFragmentManager, LargeImageDialog::class.java.simpleName)
        } else {
            showMessage(R.string.tap_for_large_image_failed)
        }
    }

    @Suppress("SameParameterValue")
    private fun showBubbleView(pos: Int) {
        // Try show badge on bottom nav bar
        val menuView= bottom_navigation.getChildAt(0) as? BottomNavigationMenuView
        val itemView = menuView?.getChildAt(pos) as? BottomNavigationItemView
        bubbleView = LayoutInflater.from(this).inflate(R.layout.menu_badge, menuView, false)
        itemView?.addView(bubbleView)
    }

    @Suppress("SameParameterValue")
    private fun removeBubbleView(pos: Int) {
        bubbleView?.let {
            // Try show badge on bottom nav bar
            val menuView= bottom_navigation.getChildAt(0) as? BottomNavigationMenuView
            val itemView = menuView?.getChildAt(pos) as? BottomNavigationItemView
            itemView?.removeView(bubbleView)
            bubbleView = null
        }
    }

    private fun showFragmentWithTag(tag: String): Boolean {
        supportFragmentManager.findFragmentByTag(tag)?.let {
            if (popBackFragmentUntil(supportFragmentManager, tag)) {
                var d = mFragmentTags.search(tag)
                while (d > 1) {
                    mFragmentTags.pop()
                    d--
                }
                return true
            }
        }
        return false
    }

    private fun showFragment(clazz: Class<*>) {
        if (!showFragmentWithTag(clazz.simpleName)) {
            addFragmentInActivity(
                supportFragmentManager,
                clazz.newInstance() as Fragment,
                R.id.clRootView
            )
        }
    }

    private fun showFragment(fragment: Fragment) {
        addFragmentInActivity(
            supportFragmentManager,
            fragment,
            R.id.clRootView
        )
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return if (bottom_navigation.selectedItemId == item.itemId) {
            false
        } else {
            when (item.itemId) {
                R.id.action_message -> {
                    removeBubbleView(2)
                    showFragment(MessageFragment::class.java)
                }
                R.id.action_forum_navigation -> showFragment(ForumNavigationV3Fragment::class.java)
                R.id.action_about_me -> showFragment(MeFragment::class.java)
                R.id.action_home -> showFragment(RecommendFragment::class.java)
                else -> { }
            }
            true
        }
    }


    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_EXTERNAL_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] != PERMISSION_GRANTED) {
                showMessage(R.string.permission_denied)
            }
        }
    }

    companion object {
        const val WRITE_EXTERNAL_CODE = 1
    }
}