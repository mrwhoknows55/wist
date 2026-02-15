package dev.avadhut.wist.core.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

// Wishlist DTOs
@Serializable
data class WishlistDto(
    val id: Int,
    val userId: Int,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime? = null
)

@Serializable
data class CreateWishlistRequest(
    val name: String
)

@Serializable
data class UpdateWishlistRequest(
    val name: String
)

// Wishlist Item DTOs
@Serializable
data class WishlistItemDto(
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

@Serializable
data class AddItemRequest(
    val url: String
)

@Serializable
data class UpdateItemRequest(
    val productName: String?,
    val productDescription: String?,
    val price: Double?,
    val currency: String?,
    val imageUrl: String?,
    val retailerName: String?,
    val retailerDomain: String?
)

// Common response types
@Serializable
data class ApiErrorResponse(
    val error: String
)

@Serializable
data class SuccessResponse(
    val success: Boolean
)
