package dev.avadhut.wist.client.sync

sealed class PendingMutation {
    data class CreateWishlist(val localId: String, val name: String) : PendingMutation()

    data class UpdateWishlist(val wishlistId: Int, val name: String) : PendingMutation()

    data class DeleteWishlist(val wishlistId: Int) : PendingMutation()

    data class AddItem(val wishlistId: Int, val url: String) : PendingMutation()

    data class UpdateItem(
        val wishlistId: Int,
        val itemId: Int,
        val productName: String? = null,
        val productDescription: String? = null,
        val price: Double? = null,
        val currency: String? = null,
        val imageUrl: String? = null,
        val retailerName: String? = null,
        val retailerDomain: String? = null,
    ) : PendingMutation()

    data class DeleteItem(val wishlistId: Int, val itemId: Int) : PendingMutation()
}
