package dev.avadhut.wist.repository

import dev.avadhut.wist.core.dto.ScrapedProductDto
import dev.avadhut.wist.database.ProductCacheTable
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.upsert
import org.slf4j.LoggerFactory
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

data class CachedProduct(
    val product: ScrapedProductDto,
    val expiresAt: LocalDateTime,
)

class ProductCacheRepository {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val json = Json { ignoreUnknownKeys = true }

    fun findByKey(key: String): CachedProduct? = transaction {
        val row = ProductCacheTable
            .selectAll()
            .where {
                (ProductCacheTable.cacheKey eq key) and (ProductCacheTable.expiresAt greater Clock.System.now()
                    .toLocalDateTime(TimeZone.UTC))
            }
            .map { it }
            .singleOrNull()
            ?: run {
                logger.debug("ProductCacheRepository: L2 miss or expired key={}", key)
                return@transaction null
            }

        val expiresAt = row[ProductCacheTable.expiresAt]
        val product = try {
            json.decodeFromString<ScrapedProductDto>(row[ProductCacheTable.productData])
        } catch (e: Exception) {
            logger.warn(
                "ProductCacheRepository: productData decode failed key={} err={}",
                key,
                e.message,
                e
            )
            return@transaction null
        }
        logger.debug("ProductCacheRepository: L2 hit key={} expiresAt={}", key, expiresAt)
        CachedProduct(product, expiresAt)
    }

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
                it[ProductCacheTable.cacheKey] = key
                it[ProductCacheTable.normalizedUrl] = normalizedUrl
                it[ProductCacheTable.originalUrl] = originalUrl
                it[ProductCacheTable.productData] =
                    json.encodeToString(ScrapedProductDto.serializer(), data)
                it[ProductCacheTable.createdAt] = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                it[ProductCacheTable.expiresAt] = expiresAt
            }
        }
        logger.debug("ProductCacheRepository: saved key={} expiresAt={}", key, expiresAt)
    }
}
