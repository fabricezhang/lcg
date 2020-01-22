package top.easelink.lcg.ui.main.recommand.view

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.fragment_recommand.*
import top.easelink.framework.topbase.ControllableFragment
import top.easelink.framework.topbase.TopActivity
import top.easelink.framework.topbase.TopFragment
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.recommand.viewmodel.RecommendViewPagerAdapter
import top.easelink.lcg.ui.search.view.SearchActivity
import top.easelink.lcg.utils.WebsiteConstant.SEARCH_URL
import top.easelink.lcg.utils.WebsiteConstant.URL_KEY


class RecommendFragment: TopFragment(), ControllableFragment {

    override fun isControllable(): Boolean {
        return true
    }

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
        (activity as TopActivity).setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.search, menu)
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val intent = Intent(context, SearchActivity::class.java)
                intent.putExtra(URL_KEY, java.lang.String.format(SEARCH_URL, query))
                context?.startActivity(intent)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.search) {
            true
        } else super.onOptionsItemSelected(item)

    }
}