package top.easelink.lcg.ui.main.me.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_notification.*
import top.easelink.framework.topbase.TopFragment
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.me.viewmodel.NotificationViewModel
import top.easelink.lcg.ui.main.me.viewmodel.NotificationsAdapter

class MeNotificationFragment: TopFragment(){

    private lateinit var notificationViewModel: NotificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationViewModel = ViewModelProviders.of(this).get(NotificationViewModel::class.java)
        setupRecyclerView()
        notificationViewModel.fetchNotifications()
    }

    private fun setupRecyclerView() {
        notification_recycler_view?.apply {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = RecyclerView.VERTICAL
            }
            itemAnimator = DefaultItemAnimator()
            adapter = NotificationsAdapter(notificationViewModel)
            notificationViewModel.apply {
                isLoading.observe(this@MeNotificationFragment, Observer {
                    if (it) {
                        loading.visibility = View.VISIBLE
                        notification_recycler_view.visibility = View.GONE
                    } else {
                        loading.visibility = View.GONE
                        notification_recycler_view.visibility = View.VISIBLE
                    }
                })
                notifications
                    .observe(this@MeNotificationFragment, Observer { model ->
                        (adapter as NotificationsAdapter).run {
                            if (itemCount > 0) {
                                append(model.notifications)
                            } else {
                                clearItems()
                                addItems(model.notifications)
                            }
                        }
                    })
            }
        }
    }
}