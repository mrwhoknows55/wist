package dev.avadhut.wist.client

import dev.avadhut.wist.client.cache.InMemoryWistCacheStore
import dev.avadhut.wist.client.util.isLikelyConnectivityFailure
import dev.avadhut.wist.core.dto.WishlistDto
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OfflineFirstWishlistDataSourceJvmTest {

    private val t = LocalDateTime(year = 2026, month = 1, day = 1, hour = 12, minute = 12)

    @Test
    fun getAllWishlists_secondReadUsesCacheWhenForceRemoteFalse() = runBlocking {
        val dto = WishlistDto(
            id = 1,
            userId = 40,
            name = "W",
            createdAt = t,
            updatedAt = t,
            deletedAt = null,
            thumbnailUrls = emptyList()
        )
        val wireJson = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
        val body = wireJson.encodeToString(ListSerializer(WishlistDto.serializer()), listOf(dto))

        var calls = 0
        val engine = MockEngine { _ ->
            calls++
            respond(
                content = body,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val http = HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
        val base = "http://test"
        val wishlists = WishlistApiClient(http, base)
        val items = WishlistItemApiClient(http, base)
        val cache = InMemoryWistCacheStore()
        val ds = OfflineFirstWishlistDataSource(wishlists, items, cache) { "42" }

        val first = ds.getAllWishlists(forceRemote = false).getOrNull()!!
        assertEquals(1, first.size)
        assertEquals(1, calls)

        val second = ds.getAllWishlists(forceRemote = false).getOrNull()!!
        assertEquals(1, second.size)
        assertEquals(1, calls)
    }

    @Test
    fun getAllWishlists_forceRemoteTrue_refetchesNetwork() = runBlocking {
        val dto = WishlistDto(1, 40, "W", t, t, null, emptyList())
        val wireJson = Json { ignoreUnknownKeys = true; encodeDefaults = true }
        val body = wireJson.encodeToString(ListSerializer(WishlistDto.serializer()), listOf(dto))

        var calls = 0
        val engine = MockEngine { _ ->
            calls++
            respond(
                content = body,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val http = HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
        }
        val ds = OfflineFirstWishlistDataSource(
            WishlistApiClient(http, "http://test"),
            WishlistItemApiClient(http, "http://test"),
            InMemoryWistCacheStore(),
        ) { "42" }

        ds.getAllWishlists(forceRemote = false).getOrNull()!!
        assertEquals(1, calls)
        ds.getAllWishlists(forceRemote = true).getOrNull()!!
        assertEquals(2, calls)
    }

    @Test
    fun getAllWishlists_fallsBackToCacheOnConnectivityFailureAfterRemoteAttempt() = runBlocking {
        val dto = WishlistDto(1, 40, "W", t, t, null, emptyList())
        val wireJson = Json { ignoreUnknownKeys = true; encodeDefaults = true }
        val body = wireJson.encodeToString(ListSerializer(WishlistDto.serializer()), listOf(dto))

        var calls = 0
        val engine = MockEngine { _ ->
            calls++
            if (calls == 1) {
                respond(
                    content = body,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            } else {
                throw IOException("network down")
            }
        }
        val http = HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
        }
        val ds = OfflineFirstWishlistDataSource(
            WishlistApiClient(http, "http://test"),
            WishlistItemApiClient(http, "http://test"),
            InMemoryWistCacheStore(),
        ) { "42" }

        ds.getAllWishlists(forceRemote = false).getOrNull()!!
        assertEquals(1, calls)

        val afterFail = ds.getAllWishlists(forceRemote = true).getOrNull()!!
        assertEquals(2, calls)
        assertEquals(1, afterFail.size)
    }

    @Test
    fun getAllWishlists_withoutScope_doesNotUseCache() = runBlocking {
        val engine = MockEngine { throw IOException("x") }
        val http = HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
        }
        val ds = OfflineFirstWishlistDataSource(
            WishlistApiClient(http, "http://test"),
            WishlistItemApiClient(http, "http://test"),
            InMemoryWistCacheStore(),
        ) { null }

        assertTrue(ds.getAllWishlists(forceRemote = false).isFailure)
    }

    @Test
    fun javaIOException_isLikelyConnectivityFailure() {
        assertTrue(IOException("reset").isLikelyConnectivityFailure())
    }
}
