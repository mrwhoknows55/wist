package dev.avadhut.wist.client

import dev.avadhut.wist.client.cache.WistCacheStore
import dev.avadhut.wist.client.cache.createDefaultWistCacheStore
import dev.avadhut.wist.client.sync.MutationOutbox
import dev.avadhut.wist.client.sync.NoOpMutationOutbox
import dev.avadhut.wist.client.util.ApiException
import dev.avadhut.wist.client.util.parseErrorMessageFromJsonBody
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

private const val WIST_CLIENT_REQUEST_TIMEOUT_MS = 240_000L
private const val WIST_CLIENT_CONNECT_TIMEOUT_MS = 20_000L
private const val WIST_CLIENT_SOCKET_TIMEOUT_MS = 240_000L

class WistApiClient(
    baseUrl: String = "https://api.wist.avadhut.dev",
    cacheStore: WistCacheStore = createDefaultWistCacheStore(),
    private val mutationOutbox: MutationOutbox = NoOpMutationOutbox,
    httpClientEngine: HttpClientEngine? = null,
) {
    private var token: String? = null
    private var cacheUserId: Int? = null
    private val cache: WistCacheStore = cacheStore

    private var homeWishlistListForceRemotePending = true
    private val detailWishlistsSyncedRemotely = mutableSetOf<Int>()

    private val _wishlistListVersion = MutableStateFlow(0)
    val wishlistListVersion: StateFlow<Int> = _wishlistListVersion.asStateFlow()

    // todo: in future use it for queueing things add offline

    private fun resetWishlistFetchSession() {
        homeWishlistListForceRemotePending = true
        detailWishlistsSyncedRemotely.clear()
        println("[Wist] WistApiClient: resetWishlistFetchSession")
    }

    private fun HttpClientConfig<*>.installWistPlugins() {
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

    val httpClient = if (httpClientEngine != null) {
        HttpClient(httpClientEngine) { installWistPlugins() }
    } else {
        HttpClient { installWistPlugins() }
    }

    val auth = AuthApiClient(httpClient, baseUrl)
    val wishlists = WishlistApiClient(httpClient, baseUrl)
    val wishlistItems = WishlistItemApiClient(httpClient, baseUrl)

    val wishlistData = OfflineFirstWishlistDataSource(
        wishlists = wishlists,
        wishlistItems = wishlistItems,
        cache = cache,
        cacheScopeId = { cacheUserId?.toString() },
    )

    fun setCacheScope(userId: Int?) {
        val prev = cacheUserId
        if (userId != null && prev != null && userId != prev) {
            println("[Wist] WistApiClient: cache scope user changed $prev -> $userId, resetting fetch session")
            resetWishlistFetchSession()
        }
        println("[Wist] WistApiClient: setCacheScope userId=$userId")
        cacheUserId = userId
    }

    fun wishlistListForceRemoteForLaunch(): Boolean {
        if (!homeWishlistListForceRemotePending) return false
        homeWishlistListForceRemotePending = false
        println("[Wist] WistApiClient: wishlistListForceRemoteForLaunch consumed -> true")
        return true
    }

    fun wishlistDetailForceRemote(wishlistId: Int): Boolean {
        val need = wishlistId !in detailWishlistsSyncedRemotely
        println("[Wist] WistApiClient: wishlistDetailForceRemote wishlistId=$wishlistId -> $need")
        return need
    }

    fun markWishlistDetailSyncedFromRemote(wishlistId: Int) {
        detailWishlistsSyncedRemotely.add(wishlistId)
        println("[Wist] WistApiClient: markWishlistDetailSyncedFromRemote wishlistId=$wishlistId")
    }

    fun invalidateWishlistDetail(wishlistId: Int) {
        detailWishlistsSyncedRemotely.remove(wishlistId)
        println("[Wist] WistApiClient: invalidateWishlistDetail wishlistId=$wishlistId")
    }

    fun invalidateWishlistList() {
        homeWishlistListForceRemotePending = true
        _wishlistListVersion.value++
        println("[Wist] WistApiClient: invalidateWishlistList version=${_wishlistListVersion.value}")
    }

    fun setToken(token: String) {
        this.token = token
    }

    fun clearToken() {
        resetWishlistFetchSession()
        cacheUserId?.toString()?.let { scopeId ->
            println("[Wist] WistApiClient: clearToken clearing cache scope=$scopeId")
            cache.clearScope(scopeId)
        }
        token = null
        cacheUserId = null
    }

    val isAuthenticated: Boolean get() = token != null

    fun close() {
        httpClient.close()
        mutationOutbox.apply { }
    }
}
