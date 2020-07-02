package top.easelink.lcg.ui.main.about.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_about.*
import top.easelink.framework.topbase.ControllableFragment
import top.easelink.framework.topbase.TopFragment
import top.easelink.lcg.R
import top.easelink.lcg.ui.webview.view.WebViewActivity
import java.util.*


class AboutFragment : TopFragment(), ControllableFragment {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun isControllable(): Boolean {
        return true
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        syncAuthorState()
        github_url.setOnClickListener {
            WebViewActivity.startWebViewWith(getString(R.string.github_url), it.context)
        }
        author_email.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.author_email))
            intent.putExtra(Intent.EXTRA_SUBJECT, "问题反馈")
//            intent.putExtra(Intent.EXTRA_TEXT, "")
            startActivity(Intent.createChooser(intent, "发送反馈邮件"))
        }
    }

    private fun syncAuthorState() {
        val hour = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
        when {
            hour < 7 -> {
                me.setAnimation(R.raw.moon_stars)
            }
            hour < 12 -> {
                me.setAnimation(R.raw.personal_mac_daytime)
            }
            hour == 12 -> {
                me.setAnimation(R.raw.sun)
            }
            hour < 18 -> {
                me.setAnimation(R.raw.personal_phone_daytime)
            }
            hour < 22 -> {
                me.setAnimation(R.raw.personal_mac_night)
            }
            else -> {
                me.setAnimation(R.raw.personal_phone_night)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): AboutFragment {
            val args = Bundle()
            val fragment = AboutFragment()
            fragment.arguments = args
            return fragment
        }
    }
}