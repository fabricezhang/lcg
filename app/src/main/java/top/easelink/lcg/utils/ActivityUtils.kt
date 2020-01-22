package top.easelink.lcg.utils

import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * The `fragment` is added to the container view with id `frameId`. The operation is
 * performed by the `fragmentManager`.
 *
 */
fun addFragmentInActivity(fragmentManager: FragmentManager,
                          fragment: Fragment,
                          frameId: Int) {
    fragmentManager
        .beginTransaction()
        .addToBackStack(fragment.javaClass.simpleName)
        .add(frameId, fragment, fragment.javaClass.simpleName)
        .commitAllowingStateLoss()
}

fun popBackFragmentUntil(fragmentManager: FragmentManager, tag: String): Boolean {
    return fragmentManager.popBackStackImmediate(tag, 0)
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
fun replaceFragmentInActivity(fragmentManager: FragmentManager, fragment: Fragment, frameId: Int) {
    val transaction = fragmentManager
        .beginTransaction()
        .replace(frameId, fragment, fragment.javaClass.simpleName)
    transaction.commitNow()
}

fun isOnMainThread(): Boolean {
    return Looper.myLooper() == Looper.getMainLooper()
}