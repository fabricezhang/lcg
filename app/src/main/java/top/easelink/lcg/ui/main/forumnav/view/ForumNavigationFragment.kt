package top.easelink.lcg.ui.main.forumnav.view

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProviders
import org.greenrobot.eventbus.EventBus
import top.easelink.framework.base.BaseFragment
import top.easelink.lcg.BR
import top.easelink.lcg.R
import top.easelink.lcg.databinding.FragmentForumsNavigationBinding
import top.easelink.lcg.ui.main.forumnav.viewmodel.ForumNavigationViewModel
import top.easelink.lcg.ui.main.model.OpenForumEvent

class ForumNavigationFragment :
    BaseFragment<FragmentForumsNavigationBinding, ForumNavigationViewModel>() {
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_forums_navigation
    }

    override fun getViewModel(): ForumNavigationViewModel {
        return ViewModelProviders.of(this).get(ForumNavigationViewModel::class.java)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    private fun setUp() {
        viewDataBinding.navigationGrid.apply {
            adapter = CustomGridViewAdapter(
                baseActivity,
                R.layout.item_forums_grid
            )
            onItemClickListener =
                AdapterView.OnItemClickListener { parent: AdapterView<*>, _: View?, position: Int, _: Long ->
                    val item = (parent.adapter as CustomGridViewAdapter).getItem(position)
                    if (item != null) {
                        EventBus.getDefault().post(OpenForumEvent(item.title, item.url))
                    }
                }
        }
        viewModel.initOptions(baseActivity)
    }

    companion object {
        @JvmStatic
        fun newInstance(): ForumNavigationFragment {
            return ForumNavigationFragment()
        }
    }
}