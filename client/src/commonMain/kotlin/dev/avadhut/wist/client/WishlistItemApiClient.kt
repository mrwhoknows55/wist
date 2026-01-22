package dev.avadhut.wist.client

import dev.avadhut.wist.client.util.runCatchingSafe
import dev.avadhut.wist.core.dto.AddItemRequest
import dev.avadhut.wist.core.dto.UpdateItemRequest
import dev.avadhut.wist.core.dto.WishlistItemDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * API client for wishlist item operations
 */
class WishlistItemApiClient(
    private val httpClient: HttpClient, private val baseUrl: String
) {
    private fun itemsPath(wishlistId: Int) = "$baseUrl/api/v1/wishlists/$wishlistId/items"
    private fun itemPath(wishlistId: Int, itemId: Int) = "${itemsPath(wishlistId)}/$itemId"

    /**
     * Get all items in a wishlist
     */
    suspend fun getWishlistItems(wishlistId: Int): Result<List<WishlistItemDto>> = runCatchingSafe {
        httpClient.get(itemsPath(wishlistId)).body()
    }

    /**
     * Add item to wishlist (triggers scraping on server)
     */
    suspend fun addItemToWishlist(wishlistId: Int, url: String): Result<WishlistItemDto> =
        runCatchingSafe {
            httpClient.post(itemsPath(wishlistId)) {
                contentType(ContentType.Application.Json)
                setBody(AddItemRequest(url))
            }.body()
        }

    /**
     * Update item details (manual edit)
     */
    suspend fun updateItem(
        wishlistId: Int,
        itemId: Int,
        productName: String? = null,
        productDescription: String? = null,
        price: Double? = null,
        currency: String? = null,
        imageUrl: String? = null,
        retailerName: String? = null,
        retailerDomain: String? = null
    ): Result<Unit> = runCatchingSafe {
        httpClient.put(itemPath(wishlistId, itemId)) {
            contentType(ContentType.Application.Json)
            setBody(
                UpdateItemRequest(
                    productName = productName,
                    productDescription = productDescription,
                    price = price,
                    currency = currency,
                    imageUrl = imageUrl,
                    retailerName = retailerName,
                    retailerDomain = retailerDomain
                )
            )
        }
    }

    /**
     * Delete item from wishlist
     */
    suspend fun deleteItem(wishlistId: Int, itemId: Int): Result<Unit> = runCatchingSafe {
        httpClient.delete(itemPath(wishlistId, itemId))
    }
}
