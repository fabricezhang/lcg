package top.easelink.lcg.ui.main.forumnav.view

import android.content.res.AssetManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kunminx.linkage.LinkageRecyclerView
import com.kunminx.linkage.bean.DefaultGroupedItem
import kotlinx.android.synthetic.main.fragment_forums_navigation_v2.view.*
import top.easelink.framework.topbase.ControllableFragment
import top.easelink.framework.topbase.TopFragment
import top.easelink.lcg.R
import top.easelink.lcg.utils.getJsonStringFromAssets

class ForumNavigationV2Fragment: TopFragment(), ControllableFragment {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forums_navigation_v2, container, false)
    }

    override fun isControllable(): Boolean {
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    private fun setUp() {
        view?.let {
            val json = getJsonStringFromAssets("forums.json", it.context)
            val items =
                Gson().fromJson<List<DefaultGroupedItem>>(
                    json,
                    genericType<List<DefaultGroupedItem>>()
                )
            it.findViewById<LinkageRecyclerView<DefaultGroupedItem.ItemInfo>>(R.id.linkageRV)
                ?.init(items, ForumsPrimaryAdapterConfig(), ForumsSecondaryAdapterConfig())
        }
    }

    private inline fun <reified T> genericType() = object: TypeToken<T>() {}.type
}