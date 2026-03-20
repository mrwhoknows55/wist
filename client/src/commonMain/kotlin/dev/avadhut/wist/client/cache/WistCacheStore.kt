package dev.avadhut.wist.client.cache

import dev.avadhut.wist.core.dto.WishlistDto
import dev.avadhut.wist.core.dto.WishlistItemDto

interface WistCacheStore {
    fun saveAllWishlists(scopeId: String, wishlists: List<WishlistDto>)

    fun getAllWishlists(scopeId: String): List<WishlistDto>?

    fun saveWishlist(scopeId: String, wishlist: WishlistDto)

    fun getWishlist(scopeId: String, id: Int): WishlistDto?

    fun saveWishlistItems(scopeId: String, wishlistId: Int, items: List<WishlistItemDto>)

    fun getWishlistItems(scopeId: String, wishlistId: Int): List<WishlistItemDto>?

    fun clearScope(scopeId: String)
}

fun createDefaultWistCacheStore(): WistCacheStore = SettingsWistCacheStore()
