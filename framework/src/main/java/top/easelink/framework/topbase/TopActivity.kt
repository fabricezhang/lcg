package top.easelink.framework.topbase

import androidx.appcompat.app.AppCompatActivity
import top.easelink.framework.utils.popBackFragmentInclusive
import java.util.*


abstract class TopActivity : AppCompatActivity(), TopFragment.Callback {

    protected val mFragmentTags = Stack<String>()

    override fun onFragmentAttached(tag: String) {
        mFragmentTags.push(tag)
    }

    override fun onFragmentDetached(tag: String): Boolean {
        supportFragmentManager.findFragmentByTag(tag)?.let {
            return popBackFragmentInclusive(supportFragmentManager, tag).also {
                if (it) mFragmentTags.clear()
            }
        }
        return false
    }
}