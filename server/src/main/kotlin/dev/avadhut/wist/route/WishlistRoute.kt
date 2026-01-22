package dev.avadhut.wist.route

import dev.avadhut.wist.core.dto.CreateWishlistRequest
import dev.avadhut.wist.core.dto.UpdateWishlistRequest
import dev.avadhut.wist.repository.WishlistRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.wishlistRoutes() {
    route("/api/v1/wishlists") {

        // Get all wishlists
        get {
            val wishlists = WishlistRepository.getAllWishlists()
            call.respond(wishlists)
        }

        // Create wishlist
        post {
            val request = call.receive<CreateWishlistRequest>()
            if (request.name.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Name is required"))
                return@post
            }

            val wishlist = WishlistRepository.createWishlist(request.name)
            call.respond(HttpStatusCode.Created, wishlist)
        }

        // Get single wishlist
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                return@get
            }

            val wishlist = WishlistRepository.getActiveWishlistById(id)
            if (wishlist == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Wishlist not found"))
                return@get
            }

            call.respond(wishlist)
        }

        // Update wishlist
        put("/{id}") {
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

            val updated = WishlistRepository.updateWishlist(id, request.name)
            if (!updated) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Wishlist not found"))
                return@put
            }

            call.respond(mapOf("success" to true))
        }

        // Delete wishlist (soft delete)
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                return@delete
            }

            val deleted = WishlistRepository.deleteWishlist(id)
            if (!deleted) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Wishlist not found"))
                return@delete
            }

            call.respond(HttpStatusCode.NoContent)
        }
    }
}
