package top.easelink.lcg.ui.main.me.view

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import kotlinx.android.synthetic.main.cardview_me_notifications.*
import kotlinx.android.synthetic.main.fragment_me.*
import kotlinx.android.synthetic.main.layout_icon_button.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import top.easelink.framework.base.BaseFragment
import top.easelink.framework.threadpool.ImmediatePool
import top.easelink.framework.threadpool.Main
import top.easelink.framework.utils.addFragmentInActivity
import top.easelink.framework.utils.bitmapBlur
import top.easelink.framework.utils.convertViewToBitmap
import top.easelink.lcg.BR
import top.easelink.lcg.LCGApp
import top.easelink.lcg.R
import top.easelink.lcg.databinding.FragmentMeBinding
import top.easelink.lcg.ui.main.about.view.AboutFragment
import top.easelink.lcg.ui.main.articles.view.FavoriteArticlesFragment
import top.easelink.lcg.ui.main.login.view.LoginHintDialog
import top.easelink.lcg.ui.main.me.viewmodel.MeViewModel
import top.easelink.lcg.ui.main.model.OpenForumEvent
import top.easelink.lcg.utils.WebsiteConstant.MY_ARTICLES_URL

class MeFragment : BaseFragment<FragmentMeBinding, MeViewModel>() {

    private val mGrayColorFilter: PorterDuffColorFilter by lazy {
        PorterDuffColorFilter(
            ContextCompat.getColor(LCGApp.getContext(), R.color.semi_gray),
            PorterDuff.Mode.SRC_ATOP
        )
    }

    private val mPojieColorFilter: PorterDuffColorFilter by lazy {
        PorterDuffColorFilter(
            ContextCompat.getColor(LCGApp.getContext(), R.color.pojie_logo),
            PorterDuff.Mode.SRC_ATOP
        )
    }

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
        return ViewModelProviders.of(this).get(MeViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setFragment(this)
        updateIconButtons()
        registerObservers()
    }

    private fun registerObservers() {
        viewModel.mLoginState.observe(viewLifecycleOwner, Observer<Boolean> {
            updateViewVisibility(it)
            if (!it) {
                // add a blur effect
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    GlobalScope.launch(ImmediatePool){
                        val bitmap = bitmapBlur(baseActivity, convertViewToBitmap(view), 20)
                        GlobalScope.launch(Main) {
                            try {
                                bitmap?.let {
                                    view?.apply {
                                        setBackgroundColor(Color.WHITE)
                                        foreground = BitmapDrawable(resources, it)
                                    }
                                }
                            } catch (e: Exception) {
                                Timber.e(e)
                            } finally {
                                LoginHintDialog().show(baseActivity.supportFragmentManager, null)
                            }
                        }
                    }
                } else {
                    LoginHintDialog().show(baseActivity.supportFragmentManager, null)
                }
            }
        })
        viewModel.mSyncFavoriteEnable.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewDataBinding.favoriteSettingsSwitch.apply {
                    playAnimation()
                    addValueCallback(
                        KeyPath("**"),
                        LottieProperty.COLOR_FILTER,
                        { mPojieColorFilter }
                    )
                }
            } else {
                viewDataBinding.favoriteSettingsSwitch.apply {
                    cancelAnimation()
                    progress = 0f
                    addValueCallback(
                        KeyPath("**"),
                        LottieProperty.COLOR_FILTER,
                        { mGrayColorFilter }
                    )
                }
            }
        })
        viewModel.mAutoSignInEnable.observe(viewLifecycleOwner, Observer {
            auto_sign_in_switch.apply {
                if (it) {
                    playAnimation()
                    addValueCallback(
                        KeyPath("**"),
                        LottieProperty.COLOR_FILTER,
                        { mPojieColorFilter }
                    )
                } else {
                    cancelAnimation()
                    progress = 0f
                    addValueCallback(
                        KeyPath("**"),
                        LottieProperty.COLOR_FILTER,
                        { mGrayColorFilter }
                    )
                }
            }
        })
    }

    private fun updateIconButtons() {
        icon_notifications?.apply {
            setOnClickListener {
                showFragment(FavoriteArticlesFragment())
                findViewById<View>(R.id.badge).visibility = View.GONE
            }
            icon_notifications.btn_icon.setImageResource(R.drawable.ic_favorite)
            icon_notifications.tv_icon.setText(R.string.ic_favorite)
        }
        icon_my_articles?.apply {
            setOnClickListener {
                EventBus
                    .getDefault()
                    .post(OpenForumEvent(
                        getString(R.string.ic_my_articles), MY_ARTICLES_URL, false
                    ))
            }
            icon_my_articles.btn_icon.setImageResource(R.drawable.ic_my_articles)
            icon_my_articles.tv_icon.setText(R.string.ic_my_articles)
        }
        icon_feedback?.apply {
            setOnClickListener {
                showFragment(AboutFragment())
            }
            icon_feedback.btn_icon.setImageResource(R.drawable.ic_about)
            icon_feedback.tv_icon.setText(R.string.ic_feedback)
        }
    }

    private fun updateViewVisibility(loggedIn: Boolean) {
        if (loggedIn) {
            me_coin_icon.setColorFilter(ContextCompat.getColor(this@MeFragment.baseActivity, R.color.pojie_logo))
            me_credit_icon.setColorFilter(ContextCompat.getColor(this@MeFragment.baseActivity, R.color.pojie_logo))
        } else {
            me_coin_icon.setColorFilter(ContextCompat.getColor(this@MeFragment.baseActivity, R.color.semi_gray))
            me_credit_icon.setColorFilter(ContextCompat.getColor(this@MeFragment.baseActivity, R.color.semi_gray))
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

    override fun onResume() {
        super.onResume()
        viewModel.fetchUserInfoDirect()
    }
}