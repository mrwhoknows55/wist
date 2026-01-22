package dev.avadhut.wist.client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Main API client for Wist
 */
class WistApiClient(
    baseUrl: String = "http://localhost:8080"
) {
    val httpClient = HttpClient {
        install(Logging)
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30_000  // 30 seconds (for scraping operations)
            connectTimeoutMillis = 10_000  // 10 seconds
            socketTimeoutMillis = 30_000   // 30 seconds
        }
    }


    val wishlists = WishlistApiClient(httpClient, baseUrl)
    val wishlistItems = WishlistItemApiClient(httpClient, baseUrl)

    fun close() {
        httpClient.close()
    }
}
