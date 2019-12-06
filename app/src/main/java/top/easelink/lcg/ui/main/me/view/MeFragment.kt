package top.easelink.lcg.ui.main.me.view

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.easelink.framework.base.BaseFragment
import top.easelink.framework.utils.bitmapBlur
import top.easelink.framework.utils.convertViewToBitmap
import top.easelink.lcg.BR
import top.easelink.lcg.LCGApp
import top.easelink.lcg.R
import top.easelink.lcg.databinding.FragmentMeBinding
import top.easelink.lcg.ui.main.login.view.LoginHintDialog
import top.easelink.lcg.ui.main.me.viewmodel.MeViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loginState.observe(this, Observer<Boolean> {
            updateViewVisibility(it)
            if (!it) {
                // add a blur effect
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isAdded && view != null) {
                    GlobalScope.launch(Dispatchers.IO){
                        bitmapBlur(baseActivity, convertViewToBitmap(view!!), 20)?.let {
                            GlobalScope.launch(Dispatchers.Main) {
                                view!!.foreground = BitmapDrawable(resources, it)
                                LoginHintDialog().show(baseActivity.supportFragmentManager, null)
                            }
                        }
                    }
                }
            }
        })
        viewModel.notificationInfo.observe(this, Observer {
            if (it.isNotEmpty()) {
                showMessage(R.string.notification_arrival)
            }
        })
        viewModel.syncFavorite.observe(this, Observer {
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
        viewModel.autoSignEnable.observe(this, Observer {
            if (it) {
                viewDataBinding.autoSignInSwitch.apply {
                    playAnimation()
                    addValueCallback(
                        KeyPath("**"),
                        LottieProperty.COLOR_FILTER,
                        { mPojieColorFilter }
                    )
                }
            } else {
                viewDataBinding.autoSignInSwitch.apply {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun updateViewVisibility(loggedIn: Boolean) {
        viewDataBinding.run {
            if (loggedIn) {
                meCoinIcon.setColorFilter(ContextCompat.getColor(this@MeFragment.baseActivity, R.color.pojie_logo))
                meCreditIcon.setColorFilter(ContextCompat.getColor(this@MeFragment.baseActivity, R.color.pojie_logo))
            } else {
                meCoinIcon.setColorFilter(ContextCompat.getColor(this@MeFragment.baseActivity, R.color.semi_gray))
                meCreditIcon.setColorFilter(ContextCompat.getColor(this@MeFragment.baseActivity, R.color.semi_gray))
            }
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchUserInfoDirect()
    }

    override fun performDependencyInjection() {

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