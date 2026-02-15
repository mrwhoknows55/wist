package dev.avadhut.wist.route

import dev.avadhut.wist.config.userId
import dev.avadhut.wist.core.dto.AddItemRequest
import dev.avadhut.wist.core.dto.UpdateItemRequest
import dev.avadhut.wist.repository.CreateWishlistItemData
import dev.avadhut.wist.repository.WishlistItemRepository
import dev.avadhut.wist.repository.WishlistRepository
import dev.avadhut.wist.service.FirecrawlException
import dev.avadhut.wist.service.WishlistItemService
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.wishlistItemRoutes(wishlistItemService: WishlistItemService) {
    authenticate("auth-jwt") {
        route("/api/v1/wishlists/{wishlistId}/items") {

            // Get all items in a wishlist
            get {
                val userId = call.userId
                val wishlistId = call.parameters["wishlistId"]?.toIntOrNull()
                if (wishlistId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid wishlist ID"))
                    return@get
                }

                // Check if wishlist exists and belongs to user
                val wishlist = WishlistRepository.getActiveWishlistById(wishlistId, userId)
                if (wishlist == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Wishlist not found"))
                    return@get
                }

                val items = WishlistItemRepository.getItemsByWishlistId(wishlistId)
                call.respond(items)
            }

            // Add item to wishlist (triggers scraping)
            post {
                val userId = call.userId
                val wishlistId = call.parameters["wishlistId"]?.toIntOrNull()
                if (wishlistId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid wishlist ID"))
                    return@post
                }

                // Check if wishlist exists and belongs to user
                val wishlist = WishlistRepository.getActiveWishlistById(wishlistId, userId)
                if (wishlist == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Wishlist not found"))
                    return@post
                }

                val request = call.receive<AddItemRequest>()
                if (request.url.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "URL is required"))
                    return@post
                }

                // Validate URL format
                try {
                    Url(request.url)
                } catch (_: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid URL format"))
                    return@post
                }

                try {
                    val item = wishlistItemService.addItemToWishlist(wishlistId, request.url)
                    call.respond(HttpStatusCode.Created, item)
                } catch (e: FirecrawlException) {
                    call.respond(
                        HttpStatusCode.BadGateway, mapOf("error" to "Scraping failed: ${e.message}")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Failed to add item: ${e.message}")
                    )
                }
            }

            // Update item (manual edit)
            put("/{itemId}") {
                val userId = call.userId
                val wishlistId = call.parameters["wishlistId"]?.toIntOrNull()
                val itemId = call.parameters["itemId"]?.toIntOrNull()

                if (wishlistId == null || itemId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid IDs"))
                    return@put
                }

                // Verify wishlist belongs to user
                val wishlist = WishlistRepository.getActiveWishlistById(wishlistId, userId)
                if (wishlist == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Wishlist not found"))
                    return@put
                }

                // Verify item belongs to wishlist
                val existingItem = WishlistItemRepository.getItemById(itemId)
                if (existingItem == null || existingItem.wishlistId != wishlistId) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Item not found"))
                    return@put
                }

                val request = call.receive<UpdateItemRequest>()
                val itemData = CreateWishlistItemData(
                    sourceUrl = existingItem.sourceUrl,
                    productName = request.productName,
                    productDescription = request.productDescription,
                    price = request.price,
                    currency = request.currency,
                    imageUrl = request.imageUrl,
                    retailerName = request.retailerName,
                    retailerDomain = request.retailerDomain
                )

                val updated = WishlistItemRepository.updateItem(itemId, itemData)
                if (!updated) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Failed to update item")
                    )
                    return@put
                }

                call.respond(mapOf("success" to true))
            }

            // Delete item
            delete("/{itemId}") {
                val userId = call.userId
                val wishlistId = call.parameters["wishlistId"]?.toIntOrNull()
                val itemId = call.parameters["itemId"]?.toIntOrNull()

                if (wishlistId == null || itemId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid IDs"))
                    return@delete
                }

                // Verify wishlist belongs to user
                val wishlist = WishlistRepository.getActiveWishlistById(wishlistId, userId)
                if (wishlist == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Wishlist not found"))
                    return@delete
                }

                // Verify item belongs to wishlist
                val existingItem = WishlistItemRepository.getItemById(itemId)
                if (existingItem == null || existingItem.wishlistId != wishlistId) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Item not found"))
                    return@delete
                }

                val deleted = WishlistItemRepository.deleteItem(itemId)
                if (!deleted) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Failed to delete item")
                    )
                    return@delete
                }

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
