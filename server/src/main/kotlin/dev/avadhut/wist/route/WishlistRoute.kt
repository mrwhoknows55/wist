package dev.avadhut.wist.route

import dev.avadhut.wist.config.userId
import dev.avadhut.wist.core.dto.CreateWishlistRequest
import dev.avadhut.wist.core.dto.UpdateWishlistRequest
import dev.avadhut.wist.repository.WishlistRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.wishlistRoutes() {
    authenticate("auth-jwt") {
        route("/api/v1/wishlists") {

            // Get all wishlists for the authenticated user
            get {
                val userId = call.userId
                val wishlists = WishlistRepository.getAllWishlists(userId)
                call.respond(wishlists)
            }

            // Create wishlist
            post {
                val userId = call.userId
                val request = call.receive<CreateWishlistRequest>()
                if (request.name.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Name is required"))
                    return@post
                }

                val wishlist = WishlistRepository.createWishlist(request.name, userId)
                call.respond(HttpStatusCode.Created, wishlist)
            }

            // Get single wishlist
            get("/{id}") {
                val userId = call.userId
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                    return@get
                }

                val wishlist = WishlistRepository.getActiveWishlistById(id, userId)
                if (wishlist == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Wishlist not found"))
                    return@get
                }

                call.respond(wishlist)
            }

            // Update wishlist
            put("/{id}") {
                val userId = call.userId
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                    return@put
                }

                val request = call.receive<UpdateWishlistRequest>()
                if (request.name.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Name is required"))
                    return@put
                }

                val updated = WishlistRepository.updateWishlist(id, request.name, userId)
                if (!updated) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Wishlist not found"))
                    return@put
                }

                call.respond(mapOf("success" to true))
            }

            // Delete wishlist (soft delete)
            delete("/{id}") {
                val userId = call.userId
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                    return@delete
                }

                val deleted = WishlistRepository.deleteWishlist(id, userId)
                if (!deleted) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Wishlist not found"))
                    return@delete
                }

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
