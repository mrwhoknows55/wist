package dev.avadhut.wist.service

import dev.avadhut.wist.core.dto.ScrapedProductDto
import dev.avadhut.wist.repository.CreateWishlistItemData
import dev.avadhut.wist.repository.ProductCacheRepository
import dev.avadhut.wist.repository.WishlistItem
import dev.avadhut.wist.repository.WishlistItemRepository
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

/**
 * Service for managing wishlist items with automatic scraping
 */
class WishlistItemService(
    private val firecrawlService: FirecrawlService,
    private val urlNormalizerService: UrlNormalizerService,
    private val productCacheRepository: ProductCacheRepository,
    private val cacheTtlHours: Long = 24L,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val l1Cache = ConcurrentHashMap<String, Pair<ScrapedProductDto, Instant>>()

    /**
     * Add an item to a wishlist by scraping the provided URL
     */
    suspend fun addItemToWishlist(wishlistId: Int, url: String): WishlistItem {
        val normalizedUrl = urlNormalizerService.normalize(url)
        val key = urlNormalizerService.cacheKey(normalizedUrl)

        // Check L1 cache
        val l1Entry = l1Cache[key]
        val scrapedProduct: ScrapedProductDto =
            if (l1Entry != null && Clock.System.now() < l1Entry.second) {
                logger.info("Cache L1 hit [key=$key]")
                l1Entry.first
            } else {
                // L1 miss or stale
                if (l1Entry != null) l1Cache.remove(key)

                // Check L2 cache
                val l2Entry = productCacheRepository.findByKey(key)
                if (l2Entry != null) {
                    logger.info("Cache L2 hit [key=$key]")
                    l1Cache[key] = Pair(l2Entry, Clock.System.now().plus(cacheTtlHours.hours))
                    l2Entry
                } else {
                    logger.info("Cache miss [key=$key] — scraping via Firecrawl")
                    val scraped = firecrawlService.scrapeProduct(url)
                    // Populate both caches (non-critical — failures are logged but don't abort item creation)
                    try {
                        productCacheRepository.save(key, normalizedUrl, url, scraped, cacheTtlHours)
                        l1Cache[key] = Pair(scraped, Clock.System.now().plus(cacheTtlHours.hours))
                    } catch (e: Exception) {
                        logger.warn("Failed to populate product cache for key $key: ${e.message}")
                    }
                    scraped
                }
            }

        // Create the wishlist item with scraped product data
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

        // Save to database
        return WishlistItemRepository.createItem(wishlistId, itemData)
    }
}
