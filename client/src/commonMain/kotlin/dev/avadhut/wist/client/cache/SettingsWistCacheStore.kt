package dev.avadhut.wist.client.cache

import com.russhwolf.settings.Settings
import dev.avadhut.wist.core.dto.WishlistDto
import dev.avadhut.wist.core.dto.WishlistItemDto
import kotlinx.serialization.builtins.ListSerializer

private val wishlistListSerializer = ListSerializer(WishlistDto.serializer())
private val itemListSerializer = ListSerializer(WishlistItemDto.serializer())

class SettingsWistCacheStore(
    private val settings: Settings = Settings(),
) : WistCacheStore {

    private fun keyAllWishlists(scopeId: String) = "$scopeId/wishlists"

    private fun keyWishlist(scopeId: String, id: Int) = "$scopeId/wishlist/$id"

    private fun keyItems(scopeId: String, wishlistId: Int) = "$scopeId/items/$wishlistId"

    override fun saveAllWishlists(scopeId: String, wishlists: List<WishlistDto>) {
        val json = wistCacheJson.encodeToString(wishlistListSerializer, wishlists)
        settings.putString(keyAllWishlists(scopeId), json)
        println("[Wist] SettingsWistCacheStore: saveAllWishlists scope=$scopeId count=${wishlists.size}")
    }

    override fun getAllWishlists(scopeId: String): List<WishlistDto>? {
        val json = settings.getStringOrNull(keyAllWishlists(scopeId)) ?: return null
        return runCatching { wistCacheJson.decodeFromString(wishlistListSerializer, json) }
            .onFailure { println("[Wist] SettingsWistCacheStore: getAllWishlists decode failed scope=$scopeId err=${it.message}") }
            .getOrNull()
    }

    override fun saveWishlist(scopeId: String, wishlist: WishlistDto) {
        val json = wistCacheJson.encodeToString(WishlistDto.serializer(), wishlist)
        settings.putString(keyWishlist(scopeId, wishlist.id), json)
        println("[Wist] SettingsWistCacheStore: saveWishlist scope=$scopeId id=${wishlist.id}")
    }

    override fun getWishlist(scopeId: String, id: Int): WishlistDto? {
        val json = settings.getStringOrNull(keyWishlist(scopeId, id)) ?: return null
        return runCatching { wistCacheJson.decodeFromString(WishlistDto.serializer(), json) }
            .onFailure { println("[Wist] SettingsWistCacheStore: getWishlist decode failed scope=$scopeId id=$id err=${it.message}") }
            .getOrNull()
    }

    override fun saveWishlistItems(scopeId: String, wishlistId: Int, items: List<WishlistItemDto>) {
        val json = wistCacheJson.encodeToString(itemListSerializer, items)
        settings.putString(keyItems(scopeId, wishlistId), json)
        println("[Wist] SettingsWistCacheStore: saveWishlistItems scope=$scopeId wishlistId=$wishlistId count=${items.size}")
    }

    override fun getWishlistItems(scopeId: String, wishlistId: Int): List<WishlistItemDto>? {
        val json = settings.getStringOrNull(keyItems(scopeId, wishlistId)) ?: return null
        return runCatching { wistCacheJson.decodeFromString(itemListSerializer, json) }
            .onFailure {
                println(
                    "[Wist] SettingsWistCacheStore: getWishlistItems decode failed scope=$scopeId wishlistId=$wishlistId err=${it.message}",
                )
            }
            .getOrNull()
    }

    override fun clearScope(scopeId: String) {
        val prefix = "$scopeId/"
        val toRemove = settings.keys.filter { it.startsWith(prefix) }
        toRemove.forEach { settings.remove(it) }
        println("[Wist] SettingsWistCacheStore: clearScope scope=$scopeId removed=${toRemove.size}")
    }
}
