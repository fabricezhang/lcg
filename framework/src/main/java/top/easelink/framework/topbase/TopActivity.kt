package top.easelink.framework.topbase

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import top.easelink.framework.utils.popBackFragmentInclusive
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.*


abstract class TopActivity : AppCompatActivity(), TopFragment.Callback {

    protected val mFragmentTags = Stack<String>()

    override fun onFragmentAttached(tag: String) {
        mFragmentTags.push(tag)
    }

    override fun onFragmentDetached(tag: String): Boolean {
        supportFragmentManager.findFragmentByTag(tag)?.let {
            return popBackFragmentInclusive(supportFragmentManager, tag)
        }
        return false
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}