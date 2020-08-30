package top.easelink.lcg.ui.main.forumnav.view

import android.content.Context
import android.util.AttributeSet
import android.widget.GridView


class WrapFixGridView(
    context: Context,
    set: AttributeSet?,
    defStyle: Int
) : GridView(context, set, defStyle) {

    constructor(context: Context, set: AttributeSet?) : this(context, set, 0)

    constructor(context: Context) : this(context, null, 0)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = MeasureSpec.makeMeasureSpec(
            Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST
        )
        super.onMeasure(widthMeasureSpec, expandSpec)
    }
}