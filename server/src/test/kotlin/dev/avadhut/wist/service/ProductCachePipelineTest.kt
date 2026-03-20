package dev.avadhut.wist.service

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours

class ProductCachePipelineTest {

    @Test
    fun normalize_amazonUrlsWithDifferentTracking_shareCacheKey() = runBlocking {
        val svc = UrlNormalizerService()
        svc.loadRules()
        val a =
            svc.normalize("https://www.amazon.in/dp/B0TEST123?tag=affiliate1&ref=sr_1_1&qid=111&sr=8-1")
        val b =
            svc.normalize("https://www.amazon.in/dp/B0TEST123?tag=affiliate2&qid=222&sr=8-2")
        assertEquals(
            svc.cacheKey(a),
            svc.cacheKey(b),
            "same product path should share cache key after ClearURLs"
        )
    }

    @Test
    fun computeL1ExpiresAt_whenRowExpiresBeforeTtlCap_usesRowExpiry() {
        val now = LocalDateTime(2025, 1, 1, 12, 0, 0, 0).toInstant(TimeZone.UTC)
        val rowExpires = LocalDateTime(2025, 1, 1, 14, 0, 0, 0)
        val expected = rowExpires.toInstant(TimeZone.UTC)
        assertEquals(
            expected,
            WishlistItemService.computeL1ExpiresAt(rowExpires, now, 24L),
        )
    }

    @Test
    fun computeL1ExpiresAt_whenRowExpiresAfterTtlCap_usesCap() {
        val now = LocalDateTime(2025, 1, 1, 12, 0, 0, 0).toInstant(TimeZone.UTC)
        val rowExpires = LocalDateTime(2025, 1, 10, 12, 0, 0, 0)
        val cap = now + 24.hours
        assertEquals(
            cap,
            WishlistItemService.computeL1ExpiresAt(rowExpires, now, 24L),
        )
    }
}
