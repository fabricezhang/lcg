package top.easelink.framework.topbase

import androidx.appcompat.app.AppCompatActivity
import top.easelink.framework.utils.popBackFragmentInclusive
import java.util.*


abstract class TopActivity : AppCompatActivity(), TopFragment.Callback {

    protected val mFragmentTags = Stack<String>()

    override fun onFragmentAttached(tag: String) {
        mFragmentTags.push(tag)
    }

    //FIXME 这里并没有清除mFragmentTags里的数据，基于MainActivity中有大量的历史逻辑（懒得改），所以需要子类自行清理
    override fun onFragmentDetached(tag: String): Boolean {
        supportFragmentManager.findFragmentByTag(tag)?.let {
            return popBackFragmentInclusive(supportFragmentManager, tag)
        }
        return false
    }
}