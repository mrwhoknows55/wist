package dev.avadhut.wist.client

import dev.avadhut.wist.client.util.runCatchingSafe
import dev.avadhut.wist.core.dto.CreateWishlistRequest
import dev.avadhut.wist.core.dto.UpdateWishlistRequest
import dev.avadhut.wist.core.dto.WishlistDto
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
 * API client for wishlist operations
 */
class WishlistApiClient(
    private val httpClient: HttpClient,
    baseUrl: String
) {
    private val apiPath = "$baseUrl/api/v1/wishlists"

    /**
     * Get all wishlists
     */
    suspend fun getAllWishlists(): Result<List<WishlistDto>> = runCatchingSafe {
        httpClient.get(apiPath).body()
    }

    /**
     * Get a single wishlist by ID
     */
    suspend fun getWishlist(id: Int): Result<WishlistDto> = runCatchingSafe {
        httpClient.get("$apiPath/$id").body()
    }

    /**
     * Create a new wishlist
     */
    suspend fun createWishlist(name: String): Result<WishlistDto> = runCatchingSafe {
        httpClient.post(apiPath) {
            contentType(ContentType.Application.Json)
            setBody(CreateWishlistRequest(name))
        }.body()
    }

    /**
     * Update wishlist name
     */
    suspend fun updateWishlist(id: Int, name: String): Result<Unit> = runCatchingSafe {
        httpClient.put("$apiPath/$id") {
            contentType(ContentType.Application.Json)
            setBody(UpdateWishlistRequest(name))
        }
    }

    /**
     * Delete wishlist (soft delete)
     */
    suspend fun deleteWishlist(id: Int): Result<Unit> = runCatchingSafe {
        httpClient.delete("$apiPath/$id")
    }
}
