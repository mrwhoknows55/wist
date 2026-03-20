package dev.avadhut.wist.service

import dev.avadhut.wist.core.dto.ScrapedProductDto
import dev.avadhut.wist.repository.CreateWishlistItemData
import dev.avadhut.wist.repository.ProductCacheRepository
import dev.avadhut.wist.repository.WishlistItem
import dev.avadhut.wist.repository.WishlistItemRepository
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

class WishlistItemService(
    private val firecrawlService: FirecrawlService,
    private val urlNormalizerService: UrlNormalizerService,
    private val productCacheRepository: ProductCacheRepository,
    private val cacheTtlHours: Long = 24L,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val l1Cache = ConcurrentHashMap<String, Pair<ScrapedProductDto, Instant>>()

    suspend fun addItemToWishlist(wishlistId: Int, url: String): WishlistItem {
        val normalizedUrl = urlNormalizerService.normalize(url)
        val key = urlNormalizerService.cacheKey(normalizedUrl)
        val scrapeUrl = normalizedUrl.ifBlank { url }
        val normalizedChanged = normalizedUrl != url

        logger.info(
            "addItemToWishlist wishlistId={} key={} normalizedChanged={} originalUrl={} normalizedUrl={}",
            wishlistId,
            key,
            normalizedChanged,
            truncateUrlForLog(url),
            truncateUrlForLog(normalizedUrl),
        )

        val l1Entry = l1Cache[key]
        val now = Clock.System.now()
        val maxL1Ttl = now.plus(cacheTtlHours.hours)

        val scrapedProduct: ScrapedProductDto =
            if (l1Entry != null && now < l1Entry.second) {
                logger.info("product cache L1 hit key={} wishlistId={}", key, wishlistId)
                l1Entry.first
            } else {
                if (l1Entry != null) {
                    logger.info("product cache L1 stale key={} wishlistId={}", key, wishlistId)
                    l1Cache.remove(key)
                }

                val l2Entry = productCacheRepository.findByKey(key)
                if (l2Entry != null) {
                    logger.info(
                        "product cache L2 hit key={} wishlistId={} expiresAt={}",
                        key,
                        wishlistId,
                        l2Entry.expiresAt
                    )
                    val l1Expires = computeL1ExpiresAt(l2Entry.expiresAt, now, cacheTtlHours)
                    l1Cache[key] = Pair(l2Entry.product, l1Expires)
                    l2Entry.product
                } else {
                    logger.info(
                        "product cache miss key={} wishlistId={} scraping Firecrawl scrapeUrl={}",
                        key,
                        wishlistId,
                        truncateUrlForLog(scrapeUrl)
                    )
                    val scraped = firecrawlService.scrapeProduct(scrapeUrl, url)
                    try {
                        productCacheRepository.save(key, normalizedUrl, url, scraped, cacheTtlHours)
                        l1Cache[key] = Pair(scraped, maxL1Ttl)
                    } catch (e: Exception) {
                        logger.warn(
                            "Failed to populate product cache for key {}: {}",
                            key,
                            e.message,
                            e
                        )
                    }
                    scraped
                }
            }

        val itemData = CreateWishlistItemData(
            sourceUrl = url,
            productName = scrapedProduct.title,
            productDescription = scrapedProduct.description,
            price = scrapedProduct.price,
            currency = scrapedProduct.currency,
            imageUrl = scrapedProduct.imageUrl,
            retailerName = scrapedProduct.retailer.name,
            retailerDomain = scrapedProduct.retailer.domain
        )

        return WishlistItemRepository.createItem(wishlistId, itemData)
    }

    companion object {
        private const val LOG_URL_MAX = 120

        internal fun truncateUrlForLog(url: String): String =
            if (url.length <= LOG_URL_MAX) url
            else url.take(LOG_URL_MAX - 3) + "..."

        internal fun computeL1ExpiresAt(
            rowExpiresAt: LocalDateTime,
            now: Instant,
            cacheTtlHours: Long,
        ): Instant {
            val rowEnd = rowExpiresAt.toInstant(TimeZone.UTC)
            val cap = now.plus(cacheTtlHours.hours)
            return minOf(rowEnd, cap)
        }
    }
}
