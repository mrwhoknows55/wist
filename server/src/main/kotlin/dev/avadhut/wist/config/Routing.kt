package dev.avadhut.wist.config

import dev.avadhut.wist.repository.ProductCacheRepository
import dev.avadhut.wist.route.authRoutes
import dev.avadhut.wist.route.healthRoutes
import dev.avadhut.wist.route.wishlistItemRoutes
import dev.avadhut.wist.route.wishlistRoutes
import dev.avadhut.wist.service.UrlNormalizerService
import dev.avadhut.wist.service.WishlistItemService
import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import kotlinx.coroutines.runBlocking

fun Application.configureRouting(services: PluginServices) {
    val urlNormalizer = UrlNormalizerService()
    val productCacheRepo = ProductCacheRepository()
    val cacheTtlHours = environment.config.propertyOrNull("cache.ttlHours")
        ?.getString()?.toLongOrNull() ?: 24L
    val wishlistItemService = WishlistItemService(
        services.firecrawlService,
        urlNormalizer,
        productCacheRepo,
        cacheTtlHours
    )

    // Load ClearURLs rules at startup (suspend → use runBlocking or launch)
    // Since configureRouting is called in module() which is NOT a coroutine, use runBlocking
    runBlocking { urlNormalizer.loadRules() }

    routing {
        healthRoutes()
        authRoutes(services.authService)
        wishlistRoutes()
        wishlistItemRoutes(wishlistItemService)
    }
}
