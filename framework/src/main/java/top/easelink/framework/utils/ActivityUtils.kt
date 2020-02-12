package top.easelink.framework.utils

import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import top.easelink.framework.topbase.ControllableFragment

/**
 * The `fragment` is added to the container view with id `frameId`. The operation is
 * performed by the `fragmentManager`.
 *
 */
fun addFragmentInActivity(fragmentManager: FragmentManager,
                          fragment: Fragment,
                          frameId: Int,
                          addToBack: Boolean = true) {
    var tag = fragment.javaClass.simpleName
    fragmentManager
        .beginTransaction()
        .apply {
            (fragment as? ControllableFragment)?.also {
                if (it.isControllable() && addToBack) {
                    tag = it.getBackStackTag()
                    addToBackStack(tag)
                } else {
                    disallowAddToBackStack()
                }
            }
        }
        .add(frameId, fragment, tag)
        .commitAllowingStateLoss()
}

fun popBackFragmentUntil(fragmentManager: FragmentManager, tag: String): Boolean {
    return fragmentManager.popBackStackImmediate(tag, 0)
}

fun popBackFragmentInclusive(fragmentManager: FragmentManager, tag: String): Boolean {
    return fragmentManager.popBackStackImmediate(tag, POP_BACK_STACK_INCLUSIVE)
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