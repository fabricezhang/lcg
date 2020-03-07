package top.easelink.lcg.ui.main.about.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import top.easelink.framework.base.BaseFragment
import top.easelink.lcg.BR
import top.easelink.lcg.R
import top.easelink.lcg.databinding.FragmentAboutBinding
import top.easelink.lcg.ui.main.about.viewmodel.AboutViewModel
import top.easelink.lcg.ui.webview.view.WebViewActivity
import java.util.*

class AboutFragment : BaseFragment<FragmentAboutBinding, AboutViewModel?>() {
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_about
    }

    override fun getViewModel(): AboutViewModel {
        return ViewModelProvider(this)[AboutViewModel::class.java]
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        syncAuthorState()
        viewDataBinding!!.githubUrl.setOnClickListener {
            WebViewActivity.startWebViewWith(getString(R.string.github_url), it.context)
        }
    }

    private fun syncAuthorState() {
        val hour = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
        when {
            hour < 7 -> {
                viewDataBinding!!.me.setAnimation(R.raw.moon_stars)
            }
            hour < 12 -> {
                viewDataBinding!!.me.setAnimation(R.raw.personal_mac_daytime)
            }
            hour == 12 -> {
                viewDataBinding!!.me.setAnimation(R.raw.sun)
            }
            hour < 18 -> {
                viewDataBinding!!.me.setAnimation(R.raw.personal_phone_daytime)
            }
            hour < 22 -> {
                viewDataBinding!!.me.setAnimation(R.raw.personal_mac_night)
            }
            else -> {
                viewDataBinding!!.me.setAnimation(R.raw.personal_phone_night)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): AboutFragment {
            val args = Bundle()
            val fragment = AboutFragment()
            fragment.arguments = args
            return fragment
        }
    }
}