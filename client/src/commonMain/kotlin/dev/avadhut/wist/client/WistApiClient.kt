package dev.avadhut.wist.client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class WistApiClient(
    baseUrl: String = "http://localhost:8080"
) {
    private var token: String? = null

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
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 30_000
        }

        defaultRequest {
            token?.let { bearerAuth(it) }
        }
    }

    val auth = AuthApiClient(httpClient, baseUrl)
    val wishlists = WishlistApiClient(httpClient, baseUrl)
    val wishlistItems = WishlistItemApiClient(httpClient, baseUrl)

    fun setToken(token: String) {
        this.token = token
    }

    fun clearToken() {
        this.token = null
    }

    val isAuthenticated: Boolean get() = token != null

    fun close() {
        httpClient.close()
    }
}
