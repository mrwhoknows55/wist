package dev.avadhut.wist.client

import kotlinx.coroutines.runBlocking

/**
 * Test main function to demonstrate client API usage
 * Run the server first, then execute this to test the API
 */
fun main() = runBlocking {
    // Initialize API client
    val apiClient = WistApiClient(baseUrl = "http://localhost:8080")

    println("=== Wist API Client Test ===\n")

    try {
        // Test 1: Create a wishlist
        println("1. Creating a new wishlist...")
        val createResult = apiClient.wishlists.createWishlist("My Test Wishlist")
        createResult.onSuccess { wishlist ->
            println("✓ Created wishlist: ${wishlist.name} (ID: ${wishlist.id})")

            // Test 2: Get all wishlists
            println("\n2. Fetching all wishlists...")
            apiClient.wishlists.getAllWishlists().onSuccess { wishlists ->
                println("✓ Found ${wishlists.size} wishlist(s):")
                wishlists.forEach { println("  - ${it.name} (ID: ${it.id})") }
            }

            // Test 3: Add an item to the wishlist (triggers scraping)
            println("\n3. Adding item to wishlist (this will scrape the URL)...")
            val testUrl = "https://www.amazon.in/dp/B0CX59RMC2"
            apiClient.wishlistItems.addItemToWishlist(wishlist.id, testUrl).onSuccess { item ->
                println("✓ Added item: ${item.productName}")
                println("  - Price: ${item.currency} ${item.price}")
                println("  - Retailer: ${item.retailerName}")

                // Test 4: Get all items in the wishlist
                println("\n4. Fetching all items in wishlist...")
                apiClient.wishlistItems.getWishlistItems(wishlist.id).onSuccess { items ->
                    println("✓ Found ${items.size} item(s):")
                    items.forEach {
                        println("  - ${it.productName}")
                        println("    URL: ${it.sourceUrl}")
                    }
                }

                // Test 5: Update item
                println("\n5. Updating item...")
                apiClient.wishlistItems.updateItem(
                    wishlistId = wishlist.id,
                    itemId = item.id,
                    productName = "Updated Product Name"
                ).onSuccess {
                    println("✓ Item updated successfully")
                }

                // Test 6: Delete item
                println("\n6. Deleting item...")
                apiClient.wishlistItems.deleteItem(wishlist.id, item.id).onSuccess {
                    println("✓ Item deleted successfully")
                }
            }.onFailure { error ->
                println("✗ Failed to add item: ${error.message}")
            }

            // Test 7: Update wishlist
            println("\n7. Updating wishlist name...")
            apiClient.wishlists.updateWishlist(wishlist.id, "Updated Wishlist Name").onSuccess {
                println("✓ Wishlist updated successfully")
            }

            // Test 8: Delete wishlist
            println("\n8. Deleting wishlist...")
            apiClient.wishlists.deleteWishlist(wishlist.id).onSuccess {
                println("✓ Wishlist deleted successfully")
            }

        }.onFailure { error ->
            println("✗ Failed to create wishlist: ${error.message}")
        }

        println("\n=== All tests completed ===")

    } catch (e: Exception) {
        println("Error: ${e.message}")
        e.printStackTrace()
    } finally {
        apiClient.close()
    }
}
