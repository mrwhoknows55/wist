package dev.avadhut.wist.repository

import dev.avadhut.wist.core.dto.ScrapedProductDto
import dev.avadhut.wist.database.ProductCacheTable
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.upsert
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

class ProductCacheRepository {

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Look up a cached product by cache key.
     * Returns null if the key is not found or the entry has expired.
     */
    fun findByKey(key: String): ScrapedProductDto? = transaction {
        ProductCacheTable
            .selectAll()
            .where {
                (ProductCacheTable.cacheKey eq key) and (ProductCacheTable.expiresAt greater Clock.System.now()
                    .toLocalDateTime(TimeZone.UTC))
            }
            .map { row -> json.decodeFromString<ScrapedProductDto>(row[ProductCacheTable.productData]) }
            .singleOrNull()
    }

    /**
     * Insert or update a cache entry for the given key.
     * If the key already exists, the product data and expiry are updated.
     */
    fun save(
        key: String,
        normalizedUrl: String,
        originalUrl: String,
        data: ScrapedProductDto,
        ttlHours: Long
    ) {
        val expiresAt = Clock.System.now()
            .plus(ttlHours.hours)
            .toLocalDateTime(TimeZone.UTC)

        transaction {
            ProductCacheTable.upsert(
                keys = arrayOf(ProductCacheTable.cacheKey),
                onUpdateExclude = listOf(
                    ProductCacheTable.createdAt,
                    ProductCacheTable.cacheKey,
                    ProductCacheTable.normalizedUrl,
                    ProductCacheTable.originalUrl
                )
            ) {
                it[cacheKey] = key
                it[ProductCacheTable.normalizedUrl] = normalizedUrl
                it[ProductCacheTable.originalUrl] = originalUrl
                it[productData] = json.encodeToString(ScrapedProductDto.serializer(), data)
                it[createdAt] = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                it[ProductCacheTable.expiresAt] = expiresAt
            }
        }
    }
}
