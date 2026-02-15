package dev.avadhut.wist.config

import dev.avadhut.wist.route.authRoutes
import dev.avadhut.wist.route.healthRoutes
import dev.avadhut.wist.route.wishlistItemRoutes
import dev.avadhut.wist.route.wishlistRoutes
import dev.avadhut.wist.service.WishlistItemService
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting(services: PluginServices) {
    val wishlistItemService = WishlistItemService(services.firecrawlService)

    routing {
        healthRoutes()
        authRoutes(services.authService)
        wishlistRoutes()
        wishlistItemRoutes(wishlistItemService)
    }
}
