package top.easelink.lcg.ui.main.forumnav.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_forums_navigation.*
import org.greenrobot.eventbus.EventBus
import top.easelink.framework.topbase.ControllableFragment
import top.easelink.framework.topbase.TopFragment
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.forumnav.viewmodel.ForumNavigationViewModel
import top.easelink.lcg.ui.main.model.OpenForumEvent

class ForumNavigationFragment : TopFragment(), ControllableFragment{

    private lateinit var mViewModel: ForumNavigationViewModel

    override fun isControllable(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this)[ForumNavigationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_forums_navigation, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    private fun setUp() {
        navigation_grid.apply {
            adapter = CustomGridViewAdapter(
                mContext,
                R.layout.item_forums_grid
            )
            onItemClickListener =
                AdapterView.OnItemClickListener { parent: AdapterView<*>, _: View?, position: Int, _: Long ->
                    val item = (parent.adapter as CustomGridViewAdapter).getItem(position)
                    if (item != null) {
                        EventBus.getDefault().post(OpenForumEvent(item.title, item.url, true))
                    }
                }
        }
        mViewModel.navigation.observe(viewLifecycleOwner, Observer {
            CustomGridViewAdapter.addForumNavigationList(navigation_grid, it)
        })
        mViewModel.initOptions(mContext)
    }
}