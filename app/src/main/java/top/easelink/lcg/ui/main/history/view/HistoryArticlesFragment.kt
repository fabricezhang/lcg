package top.easelink.lcg.ui.main.history.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_favorite_articles.recycler_view
import kotlinx.android.synthetic.main.fragment_history_articles.*
import kotlinx.coroutines.launch
import top.easelink.framework.threadpool.IOPool
import top.easelink.framework.threadpool.Main
import top.easelink.framework.topbase.ControllableFragment
import top.easelink.framework.topbase.TopFragment
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.history.model.HistoryModel
import top.easelink.lcg.ui.main.source.local.ArticlesDatabase

class HistoryArticlesFragment : TopFragment(), ControllableFragment {

    companion object {
        fun newInstance(): HistoryArticlesFragment {
            return HistoryArticlesFragment()
        }
    }

    override fun isControllable(): Boolean {
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history_articles, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        clear_all.setOnClickListener {
            launch(IOPool) {
                ArticlesDatabase.getInstance().articlesDao().deleteHistories()
            }
        }
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        recycler_view.apply {
            val mLayoutManager = LinearLayoutManager(context).also {
                it.orientation = RecyclerView.VERTICAL
            }
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()
            val mAdapter = HistoryArticlesAdapter().also {
                it.setFragmentManager(childFragmentManager)
            }
            adapter = mAdapter
            ArticlesDatabase
                .getInstance()
                .articlesDao()
                .getHistories()
                .observe(viewLifecycleOwner) { list ->
                    launch(Main) {
                        mAdapter.clearItems()
                        mAdapter.addItems(list.map {
                            HistoryModel(
                                url = it.url,
                                author = it.author,
                                title = it.title,
                                timeStamp = it.timestamp
                            )
                        })
                    }
                }
        }
    }
}