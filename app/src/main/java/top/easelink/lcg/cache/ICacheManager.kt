package top.easelink.lcg.cache

/**
 * 暂时没有特定用途，后续做模块化可以单独拆出来
 */
interface ICacheManager {
    suspend fun clearAllCaches()
}