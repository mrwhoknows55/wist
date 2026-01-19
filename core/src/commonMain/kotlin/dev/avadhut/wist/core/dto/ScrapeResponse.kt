package dev.avadhut.wist.core.dto

import kotlinx.serialization.Serializable

/**
 * Response wrapper for scrape API results.
 */
@Serializable
data class ScrapeResponse(
    val success: Boolean,
    val data: ScrapedProductDto? = null,
    val error: String? = null
)
