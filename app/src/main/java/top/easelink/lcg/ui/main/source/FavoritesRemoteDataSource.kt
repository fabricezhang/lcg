package top.easelink.lcg.ui.main.source

import io.reactivex.Observable

interface FavoritesRemoteDataSource {

    fun addFavorites(threadId: String, formHash: String): Observable<Boolean>
}