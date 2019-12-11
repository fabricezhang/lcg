package top.easelink.lcg.utils

import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.common.base.Preconditions
import top.easelink.framework.base.BaseFragment

/**
 * This provides methods to help Activities load their UI.
 */
const val TAG_PREFIX = "lcg-"
/**
 * The `fragment` is added to the container view with id `frameId`. The operation is
 * performed by the `fragmentManager`.
 *
 */
fun addFragmentInActivity(fragmentManager: FragmentManager, fragment: BaseFragment<*, *>, frameId: Int) {
    Preconditions.checkNotNull(fragmentManager)
    Preconditions.checkNotNull(fragment)
    val transaction = fragmentManager
        .beginTransaction()
        // .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
        .add(frameId, fragment, TAG_PREFIX + fragment.javaClass.simpleName)
    transaction.commitNow()
}

/**
 * The `fragment` is added to the container view with id `frameId`. The operation is
 * performed by the `fragmentManager`.
 *
 */
fun replaceFragmentInActivity(fragmentManager: FragmentManager, fragment: Fragment, tag: String?) {
    Preconditions.checkNotNull(fragmentManager)
    Preconditions.checkNotNull(fragment)
    val transaction = fragmentManager.beginTransaction()
    transaction.add(fragment, tag)
    transaction.commit()
}

fun isOnMainThread(): Boolean {
    return Looper.myLooper() == Looper.getMainLooper()
}