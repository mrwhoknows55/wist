/**
 * **Which `NavKey` import?** `androidx.navigation3.runtime.NavKey`, not a non-existent `androidx.navigation3.NavKey`.
 */
package dev.avadhut.wist.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Root sealed interface for all Wist navigation routes.
 * All route types must extend this and be @Serializable.
 */
@Serializable
sealed interface WistRoute : NavKey

// ===== Authentication Routes =====

@Serializable
data object LoginRoute : WistRoute

@Serializable
data object SignupRoute : WistRoute

// ===== Home/Wishlist Routes =====

@Serializable
data object WishlistListRoute : WistRoute

@Serializable
data class WishlistDetailRoute(val wishlistId: Int) : WistRoute

// ===== Product Detail Routes =====

/**
 * Product detail screen route.
 * Passes serializable item data to avoid losing state on recomposition.
 */
@Serializable
data class ProductDetailRoute(
    val itemId: Int,
    val itemProductName: String?,
    val itemDescription: String?,
    val itemPrice: Double?,
    val itemCurrency: String?,
    val itemSourceUrl: String,
    val itemImageUrl: String?,
    val itemRetailerName: String?,
    val itemRetailerDomain: String?,
    val itemCreatedAtEpochMillis: Long // Use Long for kotlinx.datetime serialization
) : WistRoute

// ===== WebView Routes =====

@Serializable
data class InAppWebViewRoute(val url: String) : WistRoute
