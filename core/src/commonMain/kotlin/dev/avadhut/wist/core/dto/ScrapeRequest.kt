package dev.avadhut.wist.core.dto

import kotlinx.serialization.Serializable

/**
 * Request to scrape product data from a URL.
 */
@Serializable
data class ScrapeRequest(
    val url: String
)
