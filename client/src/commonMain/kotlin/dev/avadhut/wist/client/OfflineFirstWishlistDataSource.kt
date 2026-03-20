package dev.avadhut.wist.client

import dev.avadhut.wist.client.cache.WistCacheStore
import dev.avadhut.wist.client.util.isLikelyConnectivityFailure
import dev.avadhut.wist.core.dto.WishlistDto
import dev.avadhut.wist.core.dto.WishlistItemDto

class OfflineFirstWishlistDataSource(
    private val wishlists: WishlistApiClient,
    private val wishlistItems: WishlistItemApiClient,
    private val cache: WistCacheStore,
    private val cacheScopeId: () -> String?,
) {

    private fun scopeOrNull(): String? = cacheScopeId()?.takeIf { it.isNotBlank() }

    suspend fun getAllWishlists(forceRemote: Boolean = false): Result<List<WishlistDto>> {
        val scope = scopeOrNull() ?: return fetchAllWishlistsRemoteNoScope()
        if (!forceRemote) {
            val cached = cache.getAllWishlists(scope)
            if (cached != null) {
                println("[Wist] OfflineFirst: getAllWishlists cache-first count=${cached.size} scope=$scope")
                return Result.success(cached)
            }
        }
        return fetchAllWishlistsRemote(scope)
    }

    private suspend fun fetchAllWishlistsRemoteNoScope(): Result<List<WishlistDto>> {
        val remote = wishlists.getAllWishlists()
        if (remote.isSuccess) {
            println("[Wist] OfflineFirst: getAllWishlists network ok (no scope) count=${remote.getOrNull()!!.size}")
            return remote
        }
        return remote
    }

    private suspend fun fetchAllWishlistsRemote(scope: String): Result<List<WishlistDto>> {
        val remote = wishlists.getAllWishlists()
        if (remote.isSuccess) {
            val data = remote.getOrNull()!!
            cache.saveAllWishlists(scope, data)
            println("[Wist] OfflineFirst: getAllWishlists network ok count=${data.size} scope=$scope")
            return remote
        }
        val err = remote.exceptionOrNull()!!
        if (err.isLikelyConnectivityFailure()) {
            val cached = cache.getAllWishlists(scope)
            if (cached != null) {
                println("[Wist] OfflineFirst: getAllWishlists connectivity fallback count=${cached.size} scope=$scope")
                return Result.success(cached)
            }
            println("[Wist] OfflineFirst: getAllWishlists cache miss after failure scope=$scope")
        }
        return remote
    }

    suspend fun getWishlist(id: Int, forceRemote: Boolean = false): Result<WishlistDto> {
        val scope = scopeOrNull() ?: return fetchWishlistRemoteNoScope(id)
        if (!forceRemote) {
            val cached = cache.getWishlist(scope, id)
            if (cached != null) {
                println("[Wist] OfflineFirst: getWishlist cache-first id=$id scope=$scope")
                return Result.success(cached)
            }
        }
        return fetchWishlistRemote(scope, id)
    }

    private suspend fun fetchWishlistRemoteNoScope(id: Int): Result<WishlistDto> {
        val remote = wishlists.getWishlist(id)
        if (remote.isSuccess) {
            println("[Wist] OfflineFirst: getWishlist network ok (no scope) id=$id")
        }
        return remote
    }

    private suspend fun fetchWishlistRemote(scope: String, id: Int): Result<WishlistDto> {
        val remote = wishlists.getWishlist(id)
        if (remote.isSuccess) {
            val data = remote.getOrNull()!!
            cache.saveWishlist(scope, data)
            println("[Wist] OfflineFirst: getWishlist network ok id=$id scope=$scope")
            return remote
        }
        val err = remote.exceptionOrNull()!!
        if (err.isLikelyConnectivityFailure()) {
            val cached = cache.getWishlist(scope, id)
            if (cached != null) {
                println("[Wist] OfflineFirst: getWishlist connectivity fallback id=$id scope=$scope")
                return Result.success(cached)
            }
        }
        return remote
    }

    suspend fun getWishlistItems(
        wishlistId: Int, forceRemote: Boolean = false
    ): Result<List<WishlistItemDto>> {
        val scope = scopeOrNull() ?: return fetchWishlistItemsRemoteNoScope(wishlistId)
        if (!forceRemote) {
            val cached = cache.getWishlistItems(scope, wishlistId)
            if (cached != null) {
                println("[Wist] OfflineFirst: getWishlistItems cache-first wishlistId=$wishlistId count=${cached.size} scope=$scope")
                return Result.success(cached)
            }
        }
        return fetchWishlistItemsRemote(scope, wishlistId)
    }

    private suspend fun fetchWishlistItemsRemoteNoScope(wishlistId: Int): Result<List<WishlistItemDto>> {
        val remote = wishlistItems.getWishlistItems(wishlistId)
        if (remote.isSuccess) {
            println("[Wist] OfflineFirst: getWishlistItems network ok (no scope) wishlistId=$wishlistId count=${remote.getOrNull()!!.size}")
        }
        return remote
    }

    private suspend fun fetchWishlistItemsRemote(
        scope: String, wishlistId: Int
    ): Result<List<WishlistItemDto>> {
        val remote = wishlistItems.getWishlistItems(wishlistId)
        if (remote.isSuccess) {
            val data = remote.getOrNull()!!
            cache.saveWishlistItems(scope, wishlistId, data)
            println("[Wist] OfflineFirst: getWishlistItems network ok wishlistId=$wishlistId count=${data.size} scope=$scope")
            return remote
        }
        val err = remote.exceptionOrNull()!!
        if (err.isLikelyConnectivityFailure()) {
            val cached = cache.getWishlistItems(scope, wishlistId)
            if (cached != null) {
                println("[Wist] OfflineFirst: getWishlistItems connectivity fallback wishlistId=$wishlistId count=${cached.size} scope=$scope")
                return Result.success(cached)
            }
        }
        return remote
    }
}
