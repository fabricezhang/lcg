package top.easelink.lcg.ui.main.me.view

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import kotlinx.android.synthetic.main.cardview_me_notifications.*
import kotlinx.android.synthetic.main.fragment_me.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import top.easelink.framework.base.BaseFragment
import top.easelink.framework.utils.bitmapBlur
import top.easelink.framework.utils.convertViewToBitmap
import top.easelink.lcg.BR
import top.easelink.lcg.LCGApp
import top.easelink.lcg.R
import top.easelink.lcg.databinding.FragmentMeBinding
import top.easelink.lcg.ui.main.login.view.LoginHintDialog
import top.easelink.lcg.ui.main.me.viewmodel.MeViewModel
import top.easelink.lcg.ui.main.model.OpenForumEvent
import top.easelink.lcg.utils.WebsiteConstant.MY_ARTICLES_URL
import top.easelink.lcg.utils.addFragmentInFragment
import top.easelink.lcg.utils.showMessage

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
        viewModel.mLoginState.observe(this, Observer<Boolean> {
            updateViewVisibility(it)
            if (!it) {
                // add a blur effect
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    GlobalScope.launch(Dispatchers.IO){
                        val bitmap = bitmapBlur(baseActivity, convertViewToBitmap(view), 20)
                        GlobalScope.launch(Dispatchers.Main) {
                            bitmap?.let {
                                view?.apply {
                                    setBackgroundColor(Color.WHITE)
                                    foreground = BitmapDrawable(resources, it)
                                }
                            }
                            LoginHintDialog().show(baseActivity.supportFragmentManager, null)
                        }
                    }
                } else {
                    LoginHintDialog().show(baseActivity.supportFragmentManager, null)
                }
            }
        })
        viewModel.mNotificationInfo.observe(this, Observer {
            val notificationBadge =
                cardview_me_notifications
                    .findViewById<View>(R.id.icon_notifications)
                    .findViewById<View>(R.id.badge)
            if (it.isNotEmpty()) {
                notificationBadge.visibility = View.VISIBLE
                showMessage(R.string.notification_arrival)
            } else {
                notificationBadge.visibility = View.GONE
            }
        })
        viewModel.mSyncFavoriteEnable.observe(this, Observer {
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
        viewModel.mAutoSignInEnable.observe(this, Observer {
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
                showFragment(MeNotificationFragment())
                findViewById<View>(R.id.badge).visibility = View.GONE
            }
            findViewById<ImageView>(R.id.btn_icon).setImageResource(R.drawable.ic_notifications)
            findViewById<TextView>(R.id.tv_icon).setText(R.string.ic_notifications)
        }
        icon_my_articles?.apply {
            setOnClickListener {
                EventBus
                    .getDefault()
                    .post(OpenForumEvent(
                        getString(R.string.ic_my_articles), MY_ARTICLES_URL, false))
            }
            findViewById<ImageView>(R.id.btn_icon).setImageResource(R.drawable.ic_my_articles)
            findViewById<TextView>(R.id.tv_icon).setText(R.string.ic_my_articles)
        }
        icon_feedback?.apply {
            setOnClickListener {
                showMessage(R.string.todo_tips)
            }
            findViewById<ImageView>(R.id.btn_icon).setImageResource(R.drawable.ic_comment)
            findViewById<TextView>(R.id.tv_icon).setText(R.string.ic_feedback)
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
        child_fragment_container.visibility = View.VISIBLE
        addFragmentInFragment(
            childFragmentManager,
            fragment,
            R.id.child_fragment_container
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchUserInfoDirect()
    }

    companion object {
        @JvmStatic
        fun newInstance(): MeFragment {
            val args = Bundle()
            val fragment = MeFragment()
            fragment.arguments = args
            return fragment
        }
    }
}