package dev.avadhut.wist.client

import dev.avadhut.wist.client.util.ApiException
import dev.avadhut.wist.client.util.parseErrorMessageFromJsonBody
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val WIST_CLIENT_REQUEST_TIMEOUT_MS = 240_000L
private const val WIST_CLIENT_CONNECT_TIMEOUT_MS = 20_000L
private const val WIST_CLIENT_SOCKET_TIMEOUT_MS = 240_000L

class WistApiClient(
    baseUrl: String = "https://api.wist.avadhut.dev"
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
                if (response.status.isSuccess()) return@validateResponse
                val messageText = runCatching { response.bodyAsText() }.getOrElse { "" }
                val fallbackMessage = when (response.status.value) {
                    400 -> "Something looks off with your input. Give it another try."
                    401 -> "You'll need to sign in to do that."
                    403 -> "You don't have access to this."
                    404 -> "We couldn't find what you're looking for."
                    408 -> "That took too long. Check your connection and try again."
                    409 -> "Something changed while you were working. Refresh and try again."
                    422 -> "We couldn't save that. Double-check your details and try again."
                    429 -> "You're moving too fast! Wait a moment and try again."
                    in 500..599 -> "Something went wrong on our end. Try again in a bit."
                    else -> null
                }
                val message = parseErrorMessageFromJsonBody(messageText)
                    ?: messageText.takeIf { it.isNotBlank() }
                    ?: fallbackMessage
                if (message.isNullOrBlank()) return@validateResponse
                println("[Wist] WistApiClient: HTTP ${response.status.value} -> $message")
                throw ApiException(message, httpStatusCode = response.status.value)
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = WIST_CLIENT_REQUEST_TIMEOUT_MS
            connectTimeoutMillis = WIST_CLIENT_CONNECT_TIMEOUT_MS
            socketTimeoutMillis = WIST_CLIENT_SOCKET_TIMEOUT_MS
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
