package top.easelink.framework.topbase

interface ControllableFragment {
    fun isControllable(): Boolean {
        return false
    }

    fun getBackStackTag(): String {
        return this.javaClass.simpleName
    }

}