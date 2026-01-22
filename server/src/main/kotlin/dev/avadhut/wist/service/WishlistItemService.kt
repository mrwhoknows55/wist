package dev.avadhut.wist.service

import dev.avadhut.wist.repository.CreateWishlistItemData
import dev.avadhut.wist.repository.WishlistItem
import dev.avadhut.wist.repository.WishlistItemRepository

/**
 * Service for managing wishlist items with automatic scraping
 */
class WishlistItemService(private val firecrawlService: FirecrawlService) {

    /**
     * Add an item to a wishlist by scraping the provided URL
     */
    suspend fun addItemToWishlist(wishlistId: Int, url: String): WishlistItem {
        // Scrape the product data
        val scrapedProduct = firecrawlService.scrapeProduct(url)

        // Convert to repository data model
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
