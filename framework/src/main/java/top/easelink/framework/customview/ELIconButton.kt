package top.easelink.framework.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.Button
import top.easelink.framework.R

//TODO add badge/image alignment etc
class ELIconButton : Button{
    private var resourceId = 0
    private var stringId = 0
    private var drawable: Drawable? = null
    private var text: String? = null

    private val defvalue = 40f

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        this.isClickable = true
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ELIconButton)
        resourceId = typedArray.getResourceId(R.styleable.ELIconButton_el_drawable, 0)
        if (resourceId != 0) {
            drawable = context.getDrawable(resourceId)
        }
        stringId = typedArray.getResourceId(R.styleable.ELIconButton_el_text, 0)
        if (stringId != 0) {
            text = context.getString(stringId)
        }
    }

    override fun onDraw(canvas: Canvas) {
        drawable?.let {
            // 图片顶部居中显示
            it.setBounds(0, 0, defvalue.toInt(), defvalue.toInt())
            it.draw(canvas)
        }
        text?.let {

        }
        super.onDraw(canvas)
    }
}