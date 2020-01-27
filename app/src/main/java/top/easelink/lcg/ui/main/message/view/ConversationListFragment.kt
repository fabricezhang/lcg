package top.easelink.lcg.ui.main.message.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_conversation_list.*
import top.easelink.framework.topbase.TopFragment
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.message.viewmodel.ConversationListAdapter
import top.easelink.lcg.ui.main.message.viewmodel.ConversationListViewModel

class ConversationListFragment: TopFragment() {

    private lateinit var mConversationVM: ConversationListViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mConversationVM = ViewModelProviders.of(this).get(ConversationListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_conversation_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRV()
        mConversationVM.fetchConversations()
    }

    private fun setUpRV(){
        conversation_list.apply {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = RecyclerView.VERTICAL
            }
            itemAnimator = DefaultItemAnimator()
            adapter = ConversationListAdapter(mConversationVM)
            mConversationVM.apply {
                conversations.observe(viewLifecycleOwner, Observer {
                    (adapter as ConversationListAdapter).run {
                        if (itemCount > 1) {
                            appendItems(it)
                        } else {
                            clearItems()
                            addItems(it)
                        }
                    }
                })
            }
        }
    }
}