package dev.avadhut.wist.client.cache

import dev.avadhut.wist.core.dto.WishlistDto
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class InMemoryWistCacheStoreTest {

    private val t = LocalDateTime(2024, 1, 1, 0, 0)

    @Test
    fun scopesAreIsolated() {
        val store = InMemoryWistCacheStore()
        val a = WishlistDto(1, 1, "A", t, t, null, emptyList())
        val b = WishlistDto(2, 2, "B", t, t, null, emptyList())
        store.saveAllWishlists("1", listOf(a))
        store.saveAllWishlists("2", listOf(b))
        assertEquals(1, store.getAllWishlists("1")!!.size)
        assertEquals("A", store.getAllWishlists("1")!!.first().name)
        assertEquals("B", store.getAllWishlists("2")!!.first().name)
    }

    @Test
    fun clearScope_removesOnlyThatScope() {
        val store = InMemoryWistCacheStore()
        val w = WishlistDto(1, 1, "A", t, t, null, emptyList())
        store.saveAllWishlists("10", listOf(w))
        store.saveAllWishlists("20", listOf(w.copy(id = 2, name = "B")))
        store.clearScope("10")
        assertNull(store.getAllWishlists("10"))
        assertNotNull(store.getAllWishlists("20"))
    }
}
