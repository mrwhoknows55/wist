package dev.avadhut.wist.repository

import dev.avadhut.wist.database.WishlistItems
import dev.avadhut.wist.util.currentLocalDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

@Serializable
data class WishlistItem(
    val id: Int,
    val wishlistId: Int,
    val sourceUrl: String,
    val productName: String?,
    val productDescription: String?,
    val price: Double?,
    val currency: String?,
    val imageUrl: String?,
    val retailerName: String?,
    val retailerDomain: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class CreateWishlistItemData(
    val sourceUrl: String,
    val productName: String?,
    val productDescription: String?,
    val price: Double?,
    val currency: String?,
    val imageUrl: String?,
    val retailerName: String?,
    val retailerDomain: String?
)

object WishlistItemRepository {

    /**
     * Get all items for a specific wishlist
     */
    fun getItemsByWishlistId(wishlistId: Int): List<WishlistItem> = transaction {
        WishlistItems.selectAll().where { WishlistItems.wishlistId.eq(wishlistId) }
            .orderBy(WishlistItems.createdAt to SortOrder.DESC).map { it.toWishlistItem() }
    }

    /**
     * Get a single item by ID
     */
    fun getItemById(itemId: Int): WishlistItem? = transaction {
        WishlistItems.selectAll().where { WishlistItems.id.eq(itemId) }.map { it.toWishlistItem() }
            .singleOrNull()
    }

    /**
     * Create a new wishlist item with scraped data
     */
    fun createItem(wishlistId: Int, data: CreateWishlistItemData): WishlistItem = transaction {
        val now = currentLocalDateTime()
        val id = WishlistItems.insert {
            it[WishlistItems.wishlistId] = wishlistId
            it[sourceUrl] = data.sourceUrl
            it[productName] = data.productName
            it[productDescription] = data.productDescription
            it[price] = data.price
            it[currency] = data.currency
            it[imageUrl] = data.imageUrl
            it[retailerName] = data.retailerName
            it[retailerDomain] = data.retailerDomain
            it[createdAt] = now
            it[updatedAt] = now
        }[WishlistItems.id]

        WishlistItem(
            id = id,
            wishlistId = wishlistId,
            sourceUrl = data.sourceUrl,
            productName = data.productName,
            productDescription = data.productDescription,
            price = data.price,
            currency = data.currency,
            imageUrl = data.imageUrl,
            retailerName = data.retailerName,
            retailerDomain = data.retailerDomain,
            createdAt = now,
            updatedAt = now
        )
    }

    /**
     * Update wishlist item (for manual edits)
     */
    fun updateItem(itemId: Int, data: CreateWishlistItemData): Boolean = transaction {
        val updated = WishlistItems.update({ WishlistItems.id eq itemId }) {
            it[productName] = data.productName
            it[productDescription] = data.productDescription
            it[price] = data.price
            it[currency] = data.currency
            it[imageUrl] = data.imageUrl
            it[retailerName] = data.retailerName
            it[retailerDomain] = data.retailerDomain
            it[updatedAt] = currentLocalDateTime()
        }
        updated > 0
    }

    /**
     * Delete a wishlist item
     */
    fun deleteItem(itemId: Int): Boolean = transaction {
        WishlistItems.deleteWhere { WishlistItems.id eq itemId } > 0
    }

    private fun ResultRow.toWishlistItem() = WishlistItem(
        id = this[WishlistItems.id],
        wishlistId = this[WishlistItems.wishlistId],
        sourceUrl = this[WishlistItems.sourceUrl],
        productName = this[WishlistItems.productName],
        productDescription = this[WishlistItems.productDescription],
        price = this[WishlistItems.price],
        currency = this[WishlistItems.currency],
        imageUrl = this[WishlistItems.imageUrl],
        retailerName = this[WishlistItems.retailerName],
        retailerDomain = this[WishlistItems.retailerDomain],
        createdAt = this[WishlistItems.createdAt],
        updatedAt = this[WishlistItems.updatedAt]
    )
}
