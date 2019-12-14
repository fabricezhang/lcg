package top.easelink.framework.topbase

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.*

abstract class TopActivity : AppCompatActivity(), TopFragment.Callback {

    private var mFragmentTags = Stack<String>()

    override fun onFragmentAttached(tag: String) {
        mFragmentTags.push(tag)
    }

    override fun onFragmentDetached(tag: String): Boolean {
        repeat(mFragmentTags.search(tag)) {
            mFragmentTags.pop()
        }
        supportFragmentManager.findFragmentByTag(tag)?.let {
            supportFragmentManager
                .beginTransaction()
                .remove(it)
                .commitNow()
            return true
        }
        return false
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}