package dev.avadhut.wist.repository

import dev.avadhut.wist.core.dto.WishlistDto
import dev.avadhut.wist.database.WishlistItems
import dev.avadhut.wist.database.Wishlists
import dev.avadhut.wist.util.currentLocalDateTime
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.isNotNull
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

class WishlistNameConflictException(name: String) :
    Exception("A wishlist named '$name' already exists")

object WishlistRepository {

    /**
     * Get all non-deleted wishlists for a user
     */
    fun getAllWishlists(userId: Int): List<WishlistDto> = transaction {
        val wishlists = Wishlists.selectAll()
            .where { (Wishlists.userId eq userId) and Wishlists.deletedAt.isNull() }
            .orderBy(Wishlists.createdAt to SortOrder.DESC).map { it.toWishlistDto() }

        val wishlistIds = wishlists.map { it.id }
        val thumbnailsByWishlistId = getThumbnailsByWishlistIds(wishlistIds)
        val priceRangesByWishlistId = getPriceRangesByWishlistIds(wishlistIds)

        wishlists.map {
            val (priceMin, priceMax) = priceRangesByWishlistId[it.id] ?: Pair(null, null)
            it.copy(
                thumbnailUrls = thumbnailsByWishlistId[it.id].orEmpty(),
                priceMin = priceMin,
                priceMax = priceMax
            )
        }
    }

    /**
     * Get a single wishlist by ID (including deleted ones)
     */
    fun getWishlistById(id: Int): WishlistDto? = transaction {
        Wishlists.selectAll().where { Wishlists.id eq id }.map { it.toWishlistDto() }.singleOrNull()
            ?.let { wl ->
                val (priceMin, priceMax) = getPriceRangesByWishlistIds(listOf(wl.id))[wl.id]
                    ?: Pair(null, null)
                wl.copy(
                    thumbnailUrls = getThumbnailUrls(wl.id),
                    priceMin = priceMin,
                    priceMax = priceMax
                )
            }
    }


    /**
     * Get a non-deleted wishlist by ID for a specific user
     */
    fun getActiveWishlistById(id: Int, userId: Int): WishlistDto? = transaction {
        Wishlists.selectAll().where {
            (Wishlists.id eq id) and (Wishlists.userId eq userId) and Wishlists.deletedAt.isNull()
        }.map { it.toWishlistDto() }.singleOrNull()
            ?.let { wl ->
                val (priceMin, priceMax) = getPriceRangesByWishlistIds(listOf(wl.id))[wl.id]
                    ?: Pair(null, null)
                wl.copy(
                    thumbnailUrls = getThumbnailUrls(wl.id),
                    priceMin = priceMin,
                    priceMax = priceMax
                )
            }
    }

    private fun activeWishlistWithNameExists(
        userId: Int,
        name: String,
        excludeId: Int? = null
    ): Boolean =
        Wishlists.selectAll()
            .where {
                (Wishlists.userId eq userId) and
                        (Wishlists.name eq name) and
                        Wishlists.deletedAt.isNull() and
                        (if (excludeId != null) (Wishlists.id neq excludeId) else Op.TRUE)
            }.any()

    /**
     * Create a new wishlist for a user
     */
    fun createWishlist(name: String, userId: Int): WishlistDto = transaction {
        if (activeWishlistWithNameExists(userId, name)) throw WishlistNameConflictException(name)
        val now = currentLocalDateTime()
        val id = Wishlists.insert {
            it[Wishlists.userId] = userId
            it[Wishlists.name] = name
            it[createdAt] = now
            it[updatedAt] = now
        }[Wishlists.id]

        WishlistDto(
            id = id, userId = userId, name = name, createdAt = now, updatedAt = now
        )
    }

    /**
     * Update wishlist name (only if user owns it)
     */
    fun updateWishlist(id: Int, name: String, userId: Int): Boolean = transaction {
        if (activeWishlistWithNameExists(
                userId,
                name,
                excludeId = id
            )
        ) throw WishlistNameConflictException(name)
        val updated = Wishlists.update({
            (Wishlists.id eq id) and (Wishlists.userId eq userId) and Wishlists.deletedAt.isNull()
        }) {
            it[Wishlists.name] = name
            it[updatedAt] = currentLocalDateTime()
        }
        updated > 0
    }

    /**
     * Soft delete a wishlist (only if user owns it)
     */
    fun deleteWishlist(id: Int, userId: Int): Boolean = transaction {
        val updated = Wishlists.update({
            (Wishlists.id eq id) and (Wishlists.userId eq userId) and Wishlists.deletedAt.isNull()
        }) {
            it[deletedAt] = currentLocalDateTime()
            it[updatedAt] = currentLocalDateTime()
        }
        updated > 0
    }

    private fun getPriceRangesByWishlistIds(wishlistIds: List<Int>): Map<Int, Pair<Double?, Double?>> {
        if (wishlistIds.isEmpty()) return emptyMap()
        return WishlistItems.selectAll()
            .where { (WishlistItems.wishlistId inList wishlistIds) and WishlistItems.price.isNotNull() }
            .groupBy { it[WishlistItems.wishlistId] }
            .mapValues { (_, rows) ->
                val prices = rows.mapNotNull { it[WishlistItems.price] }
                if (prices.isEmpty()) Pair(null, null)
                else Pair(prices.min(), prices.max())
            }
    }

    private fun getThumbnailUrls(wishlistId: Int): List<String> =
        WishlistItems.selectAll()
            .where { (WishlistItems.wishlistId eq wishlistId) and WishlistItems.imageUrl.isNotNull() }
            .orderBy(WishlistItems.createdAt to SortOrder.DESC)
            .limit(4)
            .mapNotNull { it[WishlistItems.imageUrl] }

    private fun getThumbnailsByWishlistIds(wishlistIds: List<Int>): Map<Int, List<String>> {
        if (wishlistIds.isEmpty()) return emptyMap()

        return WishlistItems.selectAll()
            .where { (WishlistItems.wishlistId inList wishlistIds) and WishlistItems.imageUrl.isNotNull() }
            .orderBy(WishlistItems.createdAt to SortOrder.DESC)
            .groupBy { it[WishlistItems.wishlistId] }
            .mapValues { (_, rows) ->
                rows.take(4).mapNotNull { it[WishlistItems.imageUrl] }
            }
    }

    private fun ResultRow.toWishlistDto() = WishlistDto(
        id = this[Wishlists.id],
        userId = this[Wishlists.userId],
        name = this[Wishlists.name],
        createdAt = this[Wishlists.createdAt],
        updatedAt = this[Wishlists.updatedAt],
        deletedAt = this[Wishlists.deletedAt]
    )
}
