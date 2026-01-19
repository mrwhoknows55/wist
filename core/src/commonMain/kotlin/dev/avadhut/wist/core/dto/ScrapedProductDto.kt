package dev.avadhut.wist.core.dto

import kotlinx.serialization.Serializable

/**
 * Structured product data extracted from a URL via Firecrawl.
 * Contains all the fields needed to display product information in the UI.
 */
@Serializable
data class ScrapedProductDto(
    /** Product title/name */
    val title: String,
    
    /** Product description */
    val description: String? = null,
    
    /** Main product image URL */
    val imageUrl: String? = null,
    
    /** Current price */
    val price: Double? = null,
    
    /** Currency code (e.g., "INR", "USD") */
    val currency: String? = null,
    
    /** Original price before discount (if applicable) */
    val originalPrice: Double? = null,
    
    /** Brand name */
    val brand: String? = null,
    
    /** Product category */
    val category: String? = null,
    
    /** Product highlights/features */
    val highlights: List<String> = emptyList(),
    
    /** Additional product images */
    val additionalImages: List<String> = emptyList(),
    
    /** Product rating (0-5 scale) */
    val rating: Double? = null,
    
    /** Number of reviews */
    val reviewCount: Int? = null,
    
    /** Availability status */
    val availability: String? = null,
    
    /** Retailer information */
    val retailer: RetailerInfo,
    
    /** Original source URL */
    val sourceUrl: String,
    
    /** Timestamp when data was scraped (epoch milliseconds) */
    val scrapedAt: Long
)
