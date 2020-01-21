package top.easelink.lcg.ui.main.recommand.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_recommand.*
import top.easelink.framework.topbase.ControllableFragment
import top.easelink.framework.topbase.TopFragment
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.recommand.viewmodel.RecommendViewPagerAdapter


class RecommendFragment: TopFragment(), ControllableFragment {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recommand, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    private fun setUp(){

        view_pager.adapter = RecommendViewPagerAdapter(childFragmentManager, activity)
        main_tab.setupWithViewPager(view_pager)
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        menu.clear()
//        inflater.inflate(R.menu.search, menu)
//        val item = menu.findItem(R.id.search)
//        val searchView = SearchView((mContext as TopActivity).supportActionBar?.themedContext)
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
//        item.actionView = searchView
//        searchView.apply {
//            isSubmitButtonEnabled = true
//            setOnQueryTextListener(object :
//                SearchView.OnQueryTextListener {
//                override fun onQueryTextSubmit(query: String): Boolean {
//                    if (context != null) {
//                        val intent = Intent(context, SearchActivity::class.java)
//                        intent.putExtra(WebsiteConstant.URL_KEY, String.format(WebsiteConstant.SEARCH_URL, query))
//                        context?.startActivity(intent)
//                    }
//                    return true
//                }
//                override fun onQueryTextChange(newText: String): Boolean {
//                    return false
//                }
//            })
//        }
//        return super.onCreateOptionsMenu(menu, inflater)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.search) {
            true
        } else super.onOptionsItemSelected(item)

    }
}