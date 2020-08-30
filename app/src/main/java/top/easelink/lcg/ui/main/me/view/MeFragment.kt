package top.easelink.lcg.ui.main.me.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.Coil
import coil.load
import coil.request.ImageRequest
import coil.size.OriginalSize
import coil.size.Precision
import coil.size.SizeResolver
import kotlinx.android.synthetic.main.cardview_me_notifications.*
import kotlinx.android.synthetic.main.fragment_me.*
import kotlinx.android.synthetic.main.layout_icon_button.view.*
import okhttp3.internal.wait
import org.greenrobot.eventbus.EventBus
import top.easelink.framework.base.BaseFragment
import top.easelink.framework.utils.addFragmentInActivity
import top.easelink.framework.utils.addFragmentInFragment
import top.easelink.lcg.BR
import top.easelink.lcg.R
import top.easelink.lcg.databinding.FragmentMeBinding
import top.easelink.lcg.ui.main.about.view.AboutFragment
import top.easelink.lcg.ui.main.articles.view.FavoriteArticlesFragment
import top.easelink.lcg.ui.main.follow.view.FollowFragment
import top.easelink.lcg.ui.main.login.view.LoginHintDialog
import top.easelink.lcg.ui.main.me.viewmodel.MeViewModel
import top.easelink.lcg.ui.main.model.OpenForumEvent
import top.easelink.lcg.ui.setting.view.SettingActivity
import top.easelink.lcg.utils.WebsiteConstant.MY_ARTICLES_QUERY

class MeFragment : BaseFragment<FragmentMeBinding, MeViewModel>() {

    override fun isControllable(): Boolean {
        return true
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_me
    }

    override fun getViewModel(): MeViewModel {
        return ViewModelProvider(this)[MeViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting_btn.setOnClickListener {
            startActivity(Intent(context, SettingActivity::class.java))
        }
        updateIconButtons()
        registerObservers()
    }

    private fun registerObservers() {
        viewModel.mLoginState.observe(viewLifecycleOwner) {
            updateViewVisibility(it)
            if (!it) {
                if (isAdded && isVisible && baseActivity != null) {
                    LoginHintDialog().show(baseActivity.supportFragmentManager, null)
                }
            }
        }
        viewModel.mUserInfo.observe(viewLifecycleOwner) { info ->
            info.signInStateUrl?.let { url ->
                ImageRequest.Builder(me_sign_in_state.context)
                    .data(url)
                    .allowRgb565(true)
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
                me_sign_in_state.load(info.signInStateUrl) {

                }
            }
        }
    }

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
                    .post(OpenForumEvent(
                        getString(R.string.ic_my_articles), MY_ARTICLES_QUERY, false
                    ))
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

    private fun updateViewVisibility(loggedIn: Boolean) {
        if (loggedIn) {
            me_coin_icon.setColorFilter(ContextCompat.getColor(this@MeFragment.baseActivity, R.color.pojie_logo))
            me_credit_icon.setColorFilter(ContextCompat.getColor(this@MeFragment.baseActivity, R.color.pojie_logo))
            me_anwser_rate_icon.setColorFilter(ContextCompat.getColor(this@MeFragment.baseActivity, R.color.pojie_logo))
            me_enthusiastic_icon.setColorFilter(ContextCompat.getColor(this@MeFragment.baseActivity, R.color.pojie_logo))
        } else {
            me_coin_icon.setColorFilter(ContextCompat.getColor(this@MeFragment.baseActivity, R.color.semi_gray))
            me_credit_icon.setColorFilter(ContextCompat.getColor(this@MeFragment.baseActivity, R.color.semi_gray))
            me_anwser_rate_icon.setColorFilter(ContextCompat.getColor(this@MeFragment.baseActivity, R.color.semi_gray ))
            me_enthusiastic_icon.setColorFilter(ContextCompat.getColor(this@MeFragment.baseActivity, R.color.semi_gray))
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

    override fun onResume() {
        super.onResume()
        viewModel.fetchUserInfoDirect()
    }
}