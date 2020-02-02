package top.easelink.lcg.ui.main.forumnav2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.fragment_forums_navigation_v2.*
import top.easelink.framework.topbase.ControllableFragment
import top.easelink.framework.topbase.TopFragment
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.forumnav2.viewmodel.ForumNavigationViewModelV2

class ForumNavigationV2Fragment : TopFragment(), ControllableFragment{

    private lateinit var mViewModel: ForumNavigationViewModelV2

    override fun isControllable(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this)[ForumNavigationViewModelV2::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_forums_navigation_v2, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    private fun setUp() {
        setUpRV()
        mViewModel.initOptions(mContext)
    }

    private fun setUpRV() {
        forum_rv.apply {
            layoutManager =  LinearLayoutManager(context).also {
                it.orientation = RecyclerView.VERTICAL
            }
            itemAnimator = DefaultItemAnimator()
            val multiTypeAdapter =  MultiTypeAdapter().apply {
                register(ForumNavigationItem::class.java, ForumNavigationBinder())
            }
            mViewModel.navigationItems.observe(viewLifecycleOwner, Observer {
                multiTypeAdapter.items = it
                multiTypeAdapter.notifyDataSetChanged()
            })
            adapter = multiTypeAdapter
        }
    }
}