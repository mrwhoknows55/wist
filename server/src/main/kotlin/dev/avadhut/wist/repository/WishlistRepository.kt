package dev.avadhut.wist.repository

import dev.avadhut.wist.database.Wishlists
import dev.avadhut.wist.util.currentLocalDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

@Serializable
data class Wishlist(
    val id: Int,
    val userId: Int,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime? = null
)

object WishlistRepository {

    /**
     * Get all non-deleted wishlists for a user
     */
    fun getAllWishlists(userId: Int): List<Wishlist> = transaction {
        Wishlists.selectAll()
            .where { (Wishlists.userId eq userId) and Wishlists.deletedAt.isNull() }
            .orderBy(Wishlists.createdAt to SortOrder.DESC).map { it.toWishlist() }
    }

    /**
     * Get a single wishlist by ID (including deleted ones)
     */
    fun getWishlistById(id: Int): Wishlist? = transaction {
        Wishlists.selectAll().where { Wishlists.id eq id }.map { it.toWishlist() }.singleOrNull()
    }


    /**
     * Get a non-deleted wishlist by ID for a specific user
     */
    fun getActiveWishlistById(id: Int, userId: Int): Wishlist? = transaction {
        Wishlists.selectAll().where {
            (Wishlists.id eq id) and (Wishlists.userId eq userId) and Wishlists.deletedAt.isNull()
        }.map { it.toWishlist() }.singleOrNull()
    }

    /**
     * Create a new wishlist for a user
     */
    fun createWishlist(name: String, userId: Int): Wishlist = transaction {
        val now = currentLocalDateTime()
        val id = Wishlists.insert {
            it[Wishlists.userId] = userId
            it[Wishlists.name] = name
            it[createdAt] = now
            it[updatedAt] = now
        }[Wishlists.id]

        Wishlist(
            id = id, userId = userId, name = name, createdAt = now, updatedAt = now
        )
    }

    /**
     * Update wishlist name (only if user owns it)
     */
    fun updateWishlist(id: Int, name: String, userId: Int): Boolean = transaction {
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

    private fun ResultRow.toWishlist() = Wishlist(
        id = this[Wishlists.id],
        userId = this[Wishlists.userId],
        name = this[Wishlists.name],
        createdAt = this[Wishlists.createdAt],
        updatedAt = this[Wishlists.updatedAt],
        deletedAt = this[Wishlists.deletedAt]
    )
}
