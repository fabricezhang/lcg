package top.easelink.lcg.ui.main.follow.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_following.*
import top.easelink.framework.topbase.TopFragment
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.follow.viewmodel.FollowListViewModel

class FollowDetailFragment(private val url: String): TopFragment() {

    private lateinit var mFollowVM: FollowListViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mFollowVM = ViewModelProvider(this)[FollowListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_following, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRV()
        mFollowVM.fetchData(url)
    }

    private fun setUpRV(){
        follow_list.apply {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = RecyclerView.VERTICAL
            }
            itemAnimator = DefaultItemAnimator()
            adapter = FollowListAdapter(mFollowVM, viewLifecycleOwner)
            mFollowVM.apply {
                isLoading.observe(viewLifecycleOwner, Observer {
                    if (it) {
                        loading.visibility = View.VISIBLE
                        follow_list.visibility = View.GONE
                    } else {
                        loading.visibility = View.GONE
                        follow_list.visibility = View.VISIBLE
                    }
                })
                follows.observe(viewLifecycleOwner, Observer {
                    (adapter as FollowListAdapter).run {
                        nextPageUrl = it.nextPageUrl
                        if (itemCount > 1) {
                            appendItems(it.followInfos)
                        } else {
                            clearItems()
                            addItems(it.followInfos)
                        }
                    }
                })
            }
        }
    }
}