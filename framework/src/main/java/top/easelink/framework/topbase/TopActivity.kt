package top.easelink.framework.topbase

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import com.ironz.unsafe.UnsafeAndroid
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.*


abstract class TopActivity : AppCompatActivity(), TopFragment.Callback {

    protected val mFragmentTags = Stack<String>()

    override fun onFragmentAttached(tag: String) {
        mFragmentTags.push(tag)
    }

    override fun onFragmentDetached(tag: String): Boolean {
        supportFragmentManager.findFragmentByTag(tag)?.let {
            supportFragmentManager
                .beginTransaction()
                .remove(it)
                .commitNow()
            return true
        }
        return false
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
        val unsafe = UnsafeAndroid()
        val bitmap = unsafe.allocateInstance(Bitmap::class.java)
    }
}