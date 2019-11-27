package top.easelink.lcg.ui.main.me.view

import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.easelink.framework.base.BaseFragment
import top.easelink.framework.utils.bitmapBlur
import top.easelink.framework.utils.convertViewToBitmap
import top.easelink.lcg.BR
import top.easelink.lcg.R
import top.easelink.lcg.databinding.FragmentMeBinding
import top.easelink.lcg.ui.main.login.view.LoginHintDialog
import top.easelink.lcg.ui.main.me.viewmodel.MeViewModel

class MeFragment : BaseFragment<FragmentMeBinding, MeViewModel>() {

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
        viewModel.mLoginState.observe(this, Observer<Boolean> {
            if (!it) {
                // add a blur effect
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isAdded && view != null) {
                    GlobalScope.launch(Dispatchers.IO){
                        val bitmap = bitmapBlur(baseActivity, convertViewToBitmap(view!!), 20)
                        GlobalScope.launch(Dispatchers.Main) {
                            view!!.foreground = BitmapDrawable(resources, bitmap)
                            LoginHintDialog().show(baseActivity.supportFragmentManager, null)
                        }
                    }
                }
            }
        })
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