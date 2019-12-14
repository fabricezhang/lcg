package top.easelink.lcg.ui.main.source

interface FavoritesRemoteDataSource {

    fun addFavorites(threadId: String, formHash: String): Boolean
}