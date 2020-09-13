package top.easelink.lcg.ui.main.me.view

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.Coil
import coil.load
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import kotlinx.android.synthetic.main.cardview_me_notifications.*
import kotlinx.android.synthetic.main.fragment_me.*
import kotlinx.android.synthetic.main.layout_icon_button.view.*
import org.greenrobot.eventbus.EventBus
import top.easelink.framework.topbase.ControllableFragment
import top.easelink.framework.topbase.TopFragment
import top.easelink.framework.utils.addFragmentInActivity
import top.easelink.framework.utils.addFragmentInFragment
import top.easelink.framework.utils.dpToPx
import top.easelink.lcg.R
import top.easelink.lcg.account.AccountManager
import top.easelink.lcg.ui.main.about.view.AboutFragment
import top.easelink.lcg.ui.main.articles.view.FavoriteArticlesFragment
import top.easelink.lcg.ui.main.follow.view.FollowFragment
import top.easelink.lcg.ui.main.me.viewmodel.MeViewModel
import top.easelink.lcg.ui.main.model.OpenForumEvent
import top.easelink.lcg.ui.setting.view.SettingActivity
import top.easelink.lcg.utils.WebsiteConstant.MY_ARTICLES_QUERY
import top.easelink.lcg.utils.avatar.getAvatar
import top.easelink.lcg.utils.showMessage

class MeFragment : TopFragment(), ControllableFragment {

    private lateinit var viewModel: MeViewModel

    companion object {
        private var lastFetchInfoTime = 0L
        private const val MINIMUM_REQUEST_INTERVAL = 30_000 // 30s

        private var lastShowLoginHintTime = 0L
        private const val MINIMUM_HINT_SHOW_INTERVAL = 120_000 // 120s
    }

    override fun isControllable(): Boolean {
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MeViewModel::class.java]
        setting_btn.setOnClickListener {
            startActivity(Intent(context, SettingActivity::class.java))
        }
        updateIconButtons()
        registerObservers()
        if (SystemClock.elapsedRealtime() - lastFetchInfoTime > MINIMUM_REQUEST_INTERVAL) {
            lastFetchInfoTime = SystemClock.elapsedRealtime()
            viewModel.fetchUserInfoDirect()
        }
    }

    private fun registerObservers() {
        AccountManager.isLoggedIn.observe(viewLifecycleOwner) { loggedIn ->
            updateViewState(loggedIn)
            if (!loggedIn
                && isAdded
                && isVisible
                && SystemClock.elapsedRealtime() - MINIMUM_HINT_SHOW_INTERVAL > lastShowLoginHintTime
            ) {
                lastShowLoginHintTime = SystemClock.elapsedRealtime()
                showMessage(R.string.login_hint_message)
            }
        }
        AccountManager.userInfo.observe(viewLifecycleOwner) { info ->
            me_user_name.text = info.userName
            me_user_group.text = info.groupInfo
            me_wuaicoin.text = info.wuaiCoin
            me_anwser_rate.text = info.answerRate
            me_credit.text = info.credit
            me_enthusiastic.text = info.enthusiasticValue
            info.signInStateUrl?.let { url ->
                me_sign_in_state.visibility = View.VISIBLE
                ImageRequest.Builder(me_sign_in_state.context)
                    .data(url)
                    .allowRgb565(true)
                    .lifecycle(this@MeFragment)
                    .target {
                        me_sign_in_state.apply {
                            layoutParams.width = it.intrinsicWidth / it.intrinsicHeight * height
                            setImageDrawable(it)
                        }
                    }
                    .build()
                    .let { request ->
                        Coil.imageLoader(me_sign_in_state.context).enqueue(request)
                    }
            } ?: run {
                me_sign_in_state.visibility = View.GONE
            }
            info.avatarUrl?.let {
                me_user_avatar.load(it) {
                    lifecycle(viewLifecycleOwner)
                    transformations(RoundedCornersTransformation(4.dpToPx(me_user_avatar.context)))
                    error(getAvatar())
                }
            }
        }
    }

    /**
     * Configure icon buttons on [MeFragment]
     */
    private fun updateIconButtons() {
        icon_notifications?.apply {
            setOnClickListener {
                showFragment(FavoriteArticlesFragment())
                findViewById<View>(R.id.badge).visibility = View.GONE
            }
            btn_icon.setImageResource(R.drawable.ic_favorite)
            tv_icon.setText(R.string.ic_favorite)
        }
        icon_my_articles?.apply {
            setOnClickListener {
                EventBus
                    .getDefault()
                    .post(
                        OpenForumEvent(
                            getString(R.string.ic_my_articles), MY_ARTICLES_QUERY, false
                        )
                    )
            }
            btn_icon.setImageResource(R.drawable.ic_my_articles)
            tv_icon.setText(R.string.ic_my_articles)
        }
        icon_follow?.apply {
            setOnClickListener {
                showFragment(FollowFragment())
            }
            btn_icon.setImageResource(R.drawable.ic_follow)
            tv_icon.setText(R.string.ic_follow)
        }
        icon_feedback?.apply {
            setOnClickListener {
                showFragment(AboutFragment())
            }
            btn_icon.setImageResource(R.drawable.ic_about)
            tv_icon.setText(R.string.ic_feedback)
        }
    }

    /**
     * Update views' state depending on loggedIn state
     */
    private fun updateViewState(loggedIn: Boolean) {
        // change user info's color

        if (loggedIn) {
            icon_group.visibility = View.VISIBLE
        } else {
            icon_group.visibility = View.GONE
        }

        // change feature tab's availability
        val colorTab = if (loggedIn) {
            ContextCompat.getColor(mContext, R.color.black_effective)
        } else {
            ContextCompat.getColor(mContext, R.color.semi_gray)
        }
        icon_follow.apply {
            isEnabled = loggedIn
            btn_icon.setColorFilter(colorTab)
            tv_icon.setTextColor(colorTab)
        }
        icon_my_articles.apply {
            isEnabled = loggedIn
            btn_icon.setColorFilter(colorTab)
            tv_icon.setTextColor(colorTab)
        }
    }

    private fun showFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.let {
            addFragmentInActivity(
                it,
                fragment,
                R.id.clRootView
            )
        }
    }

    private fun showChildFragment(fragment: Fragment) {
        addFragmentInFragment(childFragmentManager, fragment, R.id.child_root)
    }
}