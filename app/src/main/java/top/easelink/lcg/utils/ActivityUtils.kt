package top.easelink.lcg.utils

import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * The `fragment` is added to the container view with id `frameId`. The operation is
 * performed by the `fragmentManager`.
 *
 */
fun addFragmentInActivity(fragmentManager: FragmentManager, fragment: Fragment, frameId: Int) {
    val transaction = fragmentManager
        .beginTransaction()
        // .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
        .add(frameId, fragment, fragment.javaClass.simpleName)
    transaction.commitNow()
}

fun addFragmentInFragment(fragmentManager: FragmentManager, fragment: Fragment, frameId: Int) {
    val transaction = fragmentManager
        .beginTransaction()
        // .setCustomAnimations(R.anim.slide_left, R.anim.slide_right)
        .add(frameId, fragment, fragment.javaClass.simpleName)
    transaction.commitNow()
}

/**
 * The `fragment` is added to the container view with id `frameId`. The operation is
 * performed by the `fragmentManager`.
 *
 */
fun replaceFragmentInActivity(fragmentManager: FragmentManager, fragment: Fragment, tag: String?) {
    val transaction = fragmentManager.beginTransaction()
    transaction.add(fragment, tag)
    transaction.commit()
}

fun isOnMainThread(): Boolean {
    return Looper.myLooper() == Looper.getMainLooper()
}