package top.easelink.lcg.ui.main.article.view

import android.view.View
import com.tencent.stat.StatService
import org.greenrobot.eventbus.EventBus
import top.easelink.framework.customview.htmltextview.ClickablePreCodeSpan
import top.easelink.lcg.R
import top.easelink.lcg.event.EVENT_TAP_FOR_CODE
import top.easelink.lcg.ui.main.model.OpenHalfWebViewFragmentEvent
import top.easelink.lcg.utils.showMessage

internal class ClickablePreCodeSpanImpl : ClickablePreCodeSpan {

    constructor()

    private constructor(html: String?) {
        this.html = html
    }

    override fun newInstance(): ClickablePreCodeSpan {
        return ClickablePreCodeSpanImpl(getHtml())
    }

    override fun onClick(view: View) {
        StatService.trackCustomEvent(view.context, EVENT_TAP_FOR_CODE)
        html?.let {
            EventBus.getDefault().post(OpenHalfWebViewFragmentEvent(it))
        } ?: run {
            showMessage(R.string.general_error)
        }
    }
}