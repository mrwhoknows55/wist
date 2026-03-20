package dev.avadhut.wist.client.cache

import dev.avadhut.wist.core.dto.WishlistDto
import dev.avadhut.wist.core.dto.WishlistItemDto
import kotlinx.serialization.builtins.ListSerializer

private val wishlistListSerializer = ListSerializer(WishlistDto.serializer())
private val itemListSerializer = ListSerializer(WishlistItemDto.serializer())

class InMemoryWistCacheStore : WistCacheStore {
    private val strings = mutableMapOf<String, String>()

    override fun saveAllWishlists(scopeId: String, wishlists: List<WishlistDto>) {
        strings["$scopeId/wishlists"] =
            wistCacheJson.encodeToString(wishlistListSerializer, wishlists)
    }

    override fun getAllWishlists(scopeId: String): List<WishlistDto>? {
        val json = strings["$scopeId/wishlists"] ?: return null
        return runCatching {
            wistCacheJson.decodeFromString(
                wishlistListSerializer,
                json
            )
        }.getOrNull()
    }

    override fun saveWishlist(scopeId: String, wishlist: WishlistDto) {
        strings["$scopeId/wishlist/${wishlist.id}"] =
            wistCacheJson.encodeToString(WishlistDto.serializer(), wishlist)
    }

    override fun getWishlist(scopeId: String, id: Int): WishlistDto? {
        val json = strings["$scopeId/wishlist/$id"] ?: return null
        return runCatching {
            wistCacheJson.decodeFromString(
                WishlistDto.serializer(),
                json
            )
        }.getOrNull()
    }

    override fun saveWishlistItems(scopeId: String, wishlistId: Int, items: List<WishlistItemDto>) {
        strings["$scopeId/items/$wishlistId"] =
            wistCacheJson.encodeToString(itemListSerializer, items)
    }

    override fun getWishlistItems(scopeId: String, wishlistId: Int): List<WishlistItemDto>? {
        val json = strings["$scopeId/items/$wishlistId"] ?: return null
        return runCatching { wistCacheJson.decodeFromString(itemListSerializer, json) }.getOrNull()
    }

    override fun clearScope(scopeId: String) {
        val prefix = "$scopeId/"
        strings.keys.filter { it.startsWith(prefix) }.forEach { strings.remove(it) }
    }
}
