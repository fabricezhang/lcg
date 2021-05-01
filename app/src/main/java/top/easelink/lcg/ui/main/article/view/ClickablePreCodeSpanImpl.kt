package top.easelink.lcg.ui.main.article.view

import android.view.View
import org.greenrobot.eventbus.EventBus
import top.easelink.framework.customview.htmltextview.ClickablePreCodeSpan
import top.easelink.lcg.R
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
        html?.let {
            EventBus.getDefault().post(OpenHalfWebViewFragmentEvent(it))
        } ?: run {
            showMessage(R.string.general_error)
        }
    }
}