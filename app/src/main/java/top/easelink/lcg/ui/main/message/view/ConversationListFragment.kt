package top.easelink.lcg.ui.main.message.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import top.easelink.framework.topbase.TopFragment
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.message.viewmodel.ConversationListViewModel

class ConversationListFragment: TopFragment() {

    private lateinit var pmVM: ConversationListViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pmVM = ViewModelProviders.of(this).get(ConversationListViewModel::class.java)
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
    }

    private fun setUpRV(){

    }
}