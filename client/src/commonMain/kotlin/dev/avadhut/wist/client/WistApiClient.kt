package dev.avadhut.wist.client

import dev.avadhut.wist.client.util.ApiException
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
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

        HttpResponseValidator {
            validateResponse { response ->
                val message = when (response.status.value) {
                    400 -> "Something looks off with your input. Give it another try."
                    401 -> "You'll need to sign in to do that."
                    403 -> "You don't have access to this."
                    404 -> "We couldn't find what you're looking for."
                    408 -> "That took too long. Check your connection and try again."
                    409 -> "Something changed while you were working. Refresh and try again."
                    422 -> "We couldn't save that. Double-check your details and try again."
                    429 -> "You're moving too fast! Wait a moment and try again."
                    in 500..599 -> "Something went wrong on our end. Try again in a bit."
                    else -> return@validateResponse
                }
                throw ApiException(message)
            }
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
