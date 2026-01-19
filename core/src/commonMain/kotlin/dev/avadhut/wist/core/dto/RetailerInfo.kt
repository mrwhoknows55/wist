package dev.avadhut.wist.core.dto

import kotlinx.serialization.Serializable

/**
 * Retailer information extracted from the source URL domain.
 */
@Serializable
data class RetailerInfo(
    val name: String,
    val domain: String,
    val logoUrl: String? = null,
    val brandColor: String? = null
)
