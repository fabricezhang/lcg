package top.easelink.lcg.ui.profile.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.android.synthetic.main.dialog_profile.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import top.easelink.framework.threadpool.ApiPool
import top.easelink.framework.utils.dpToPx
import top.easelink.framework.utils.getStatusBarHeight
import top.easelink.lcg.R
import top.easelink.lcg.mta.EVENT_OPEN_PROFILE
import top.easelink.lcg.mta.EVENT_SUBSCRIBE_USER
import top.easelink.lcg.mta.sendEvent
import top.easelink.lcg.network.Client
import top.easelink.lcg.ui.main.source.parseExtraUserInfoProfilePage
import top.easelink.lcg.ui.profile.model.PopUpProfileInfo
import top.easelink.lcg.utils.showMessage

class PopUpProfileDialog(
    private val popUpInfo: PopUpProfileInfo
): DialogFragment() {

    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return View.inflate(mContext, R.layout.dialog_profile, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        extra_info_grid.adapter = UserInfoGridViewAdapter(view.context, R.layout.item_profile_user_info).also {
            popUpInfo.extraUserInfo?.let { info ->
                it.addAll(parseExtraUserInfoProfilePage(info))
            }
        }
        username.text = popUpInfo.userName
        popUpInfo.followInfo?.let { info ->
            subscribe_btn.visibility = View.VISIBLE
            subscribe_btn.text = info.first
            subscribe_btn.setOnClickListener{
                onSubscribeClicked(info.second)
            }
        }?:run {
            subscribe_btn.visibility = View.GONE
        }

        Glide.with(mContext)
            .load(popUpInfo.imageUrl)
            .transform(RoundedCorners(4.dpToPx(mContext).toInt()))
            .into(profile_avatar)

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setGravity(Gravity.START or Gravity.TOP)
            val lp = it.attributes
            it.attributes = lp.apply {
                val padding = 10.dpToPx(mContext).toInt()
                x = popUpInfo.imageX - padding
                y = popUpInfo.imageY - getStatusBarHeight(mContext) - padding
                width = ViewGroup.LayoutParams.WRAP_CONTENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }
        dialog?.setCanceledOnTouchOutside(true)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
        sendEvent(EVENT_OPEN_PROFILE)
    }

    private fun onSubscribeClicked(url: String) {
        sendEvent(EVENT_SUBSCRIBE_USER)
        GlobalScope.launch(ApiPool){
            try {
                Client.sendGetRequestWithQuery(url).let {
                    it.getElementById("messagetext")?.text()?.let {msg ->
                        showMessage(msg)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                showMessage(R.string.subscribe_failed)
            }
        }
    }
}